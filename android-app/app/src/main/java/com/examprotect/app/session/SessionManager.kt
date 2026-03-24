package com.examprotect.app.session

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.examprotect.app.domain.model.ExamPolicy
import com.examprotect.app.kiosk.KioskController
import com.examprotect.app.violation.ViolationTracker
import com.examprotect.app.data.session.SessionRepository
import com.examprotect.app.data.network.HeartbeatRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.time.Instant

class SessionManager(
  private val violation: ViolationTracker,
  private val kiosk: KioskController,
  private val repo: SessionRepository
) : DefaultLifecycleObserver {
  private val scope = CoroutineScope(Dispatchers.IO)
  private var hbJob: Job? = null
  private var backgroundSince: Long? = null
  private val active = AtomicBoolean(false)

  fun start(activity: Activity, policy: ExamPolicy) {
    active.set(true)
    if (policy.managedMode) kiosk.enterKioskIfPermitted(activity)
    startHeartbeat(policy.heartbeatSeconds)
  }

  fun stop(activity: Activity) {
    active.set(false)
    hbJob?.cancel()
    kiosk.exitKioskIfActive(activity)
  }

  private fun startHeartbeat(seconds: Int) {
    hbJob?.cancel()
    hbJob = scope.launch {
      while (isActive && active.get()) {
        delay(seconds * 1000L)
        runCatching {
          val sid = repo.currentSessionId() ?: return@runCatching
          val body = HeartbeatRequest(
            timestamp = Instant.now().toString(),
            violations = 0,
            events = emptyList(),
            metrics = emptyMap(),
            nonce = "n"
          )
          repo.heartbeat(sid, body)
        }
      }
    }
  }

  override fun onPause(owner: LifecycleOwner) {
    backgroundSince = System.currentTimeMillis()
    violation.log("APP_BACKGROUND", null)
  }

  override fun onResume(owner: LifecycleOwner) {
    val since = backgroundSince ?: return
    backgroundSince = null
    val elapsed = (System.currentTimeMillis() - since) / 1000
    violation.log("APP_FOREGROUND", "elapsed=$elapsed")
  }
}
