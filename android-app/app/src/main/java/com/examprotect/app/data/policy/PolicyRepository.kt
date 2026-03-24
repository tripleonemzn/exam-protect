package com.examprotect.app.data.policy

import android.content.Context
import com.examprotect.app.BuildConfig
import com.examprotect.app.data.network.ApiService
import com.examprotect.app.data.network.PolicyPayload
import com.examprotect.app.data.network.toDomain
import com.examprotect.app.domain.model.ExamPolicy
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PolicyRepository @Inject constructor(
  private val api: ApiService,
  private val context: Context,
  private val moshi: Moshi
) {
  suspend fun loadPolicy(examId: String): ExamPolicy = withContext(Dispatchers.IO) {
    if (BuildConfig.USE_LOCAL_POLICY) {
      val json = context.assets.open("sample-policy.json").bufferedReader().use { it.readText() }
      val adapter = moshi.adapter(PolicyPayload::class.java)
      val payload = adapter.fromJson(json) ?: error("Invalid policy JSON")
      return@withContext payload.toDomain()
    } else {
      val resp = api.getExamConfig(examId)
      val adapter = moshi.adapter(PolicyPayload::class.java)
      val payload = adapter.fromJson(resp.jws) ?: error("Invalid JWS payload placeholder")
      return@withContext payload.toDomain()
    }
  }

  fun hashPayload(json: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val d = md.digest(json.toByteArray())
    return d.joinToString("") { "%02x".format(it) }
  }
}
