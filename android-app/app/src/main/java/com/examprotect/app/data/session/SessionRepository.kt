package com.examprotect.app.data.session

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.examprotect.app.data.network.ApiService
import com.examprotect.app.data.network.CompleteRequest
import com.examprotect.app.data.network.HeartbeatRequest
import com.examprotect.app.data.network.SessionStartRequest
import com.examprotect.app.data.network.SessionStartResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore("session")

@Singleton
class SessionRepository @Inject constructor(
  @ApplicationContext private val context: Context,
  private val api: ApiService
) {
  private val KEY_SESSION_ID = stringPreferencesKey("session_id")
  private val KEY_ENTRY_PIN = stringPreferencesKey("entry_pin")

  fun sessionIdFlow(): Flow<String?> =
    context.sessionDataStore.data
      .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
      .map { it[KEY_SESSION_ID] }

  suspend fun startSession(deviceId: String, examId: String, mode: String, appVersion: String, capabilities: Map<String, Any?>, policyHash: String, entryPin: String? = null): SessionStartResponse {
    val currentPin = entryPin ?: context.sessionDataStore.data.first()[KEY_ENTRY_PIN]
    val req = SessionStartRequest(deviceId, examId, mode, appVersion, capabilities, policyHash, currentPin)
    Log.d("EXAMPROTECT", "startSession examId=$examId deviceId=$deviceId entryPin=${currentPin?.let { "***" }}")
    val resp = api.startSession(req)
    Log.d("EXAMPROTECT", "startSession resp sessionId=${resp.sessionId} hb=${resp.heartbeatSeconds}")
    context.sessionDataStore.edit { it[KEY_SESSION_ID] = resp.sessionId }
    return resp
  }

  suspend fun heartbeat(sessionId: String, body: HeartbeatRequest) {
    api.heartbeat(sessionId, body)
  }

  suspend fun complete(sessionId: String, endedReason: String, violations: Int, summary: Map<String, Any?>) {
    api.complete(sessionId, CompleteRequest(endedReason, violations, summary))
    context.sessionDataStore.edit { it.remove(KEY_SESSION_ID) }
  }

  suspend fun currentSessionId(): String? = sessionIdFlow().first()

  suspend fun setEntryPin(pin: String) {
    context.sessionDataStore.edit { it[KEY_ENTRY_PIN] = pin }
  }
}
