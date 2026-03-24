package com.examprotect.app.kiosk

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

class KioskController(private val context: Context) {
  fun isLockTaskPermitted(): Boolean {
    val dpm: DevicePolicyManager? = context.getSystemService()
    return dpm?.isLockTaskPermitted(context.packageName) == true
  }

  fun enterKioskIfPermitted(activity: Activity) {
    if (isLockTaskPermitted()) {
      // Perlu perangkat terprovisi sebagai device-owner dan app termasuk allowlist lock-task.
      activity.startLockTask()
    }
  }

  fun exitKioskIfActive(activity: Activity) {
    runCatching { activity.stopLockTask() }
  }
}
