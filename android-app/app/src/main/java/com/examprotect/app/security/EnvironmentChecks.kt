package com.examprotect.app.security

import android.os.Build
import android.os.Debug
import java.io.File

object EnvironmentChecks {
  fun isDebuggerAttached(): Boolean = Debug.isDebuggerConnected()
  fun isProbablyEmulator(): Boolean {
    val prop = Build.FINGERPRINT + Build.MODEL + Build.PRODUCT + Build.MANUFACTURER
    return prop.contains("generic", true) || prop.contains("emulator", true) || prop.contains("sdk_gphone", true)
  }
  fun isRootSuspected(): Boolean {
    val paths = listOf(
      "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/app/Superuser.apk"
    )
    return paths.any { File(it).exists() }
  }
}
