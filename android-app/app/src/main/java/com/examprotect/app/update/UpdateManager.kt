package com.examprotect.app.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.EntryPointAccessors
import com.examprotect.app.di.ApiEntryPoint

object UpdateManager {
  private const val CHANNEL_ID = "updates"
  private const val NOTIF_ID = 1001

  suspend fun checkAndNotify(context: Context) {
    val ep = EntryPointAccessors.fromApplication(context, ApiEntryPoint::class.java)
    val api = ep.api()
    runCatching {
      val info = api.getUpdate()
      val currentCode = com.examprotect.app.BuildConfig.VERSION_CODE
      val hasUrl = !info.downloadUrl.isNullOrBlank()
      if (info.versionCode > currentCode && hasUrl) {
        notify(context, info.versionName ?: "Update", info.notes ?: "Versi baru tersedia", info.downloadUrl!!)
      }
    }
  }

  private fun notify(context: Context, title: String, text: String, url: String) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val ch = NotificationChannel(CHANNEL_ID, "Updates", NotificationManager.IMPORTANCE_DEFAULT)
      nm.createNotificationChannel(ch)
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val n = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.stat_sys_download_done)
      .setContentTitle("Update $title")
      .setContentText(text)
      .setAutoCancel(true)
      .setContentIntent(pi)
      .build()
    nm.notify(NOTIF_ID, n)
  }
}
