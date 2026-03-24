package com.examprotect.app.ui.screens

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.WebViewCompat
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.examprotect.app.domain.model.ExamPolicy
import com.examprotect.app.kiosk.KioskController
import com.examprotect.app.session.SessionManager
import com.examprotect.app.violation.ViolationTracker
import dagger.hilt.android.EntryPointAccessors
import com.examprotect.app.di.SessionEntryPoint
import com.examprotect.app.data.session.SessionRepository
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import android.util.Log

@Composable
fun ExamScreen(onFinish: () -> Unit, initialUrl: String? = null, initialExamId: String? = null) {
  val context = LocalContext.current
  val activity = context as Activity
  val lifecycleOwner = LocalLifecycleOwner.current
  fun normalizeUrl(u: String?): String {
    if (u.isNullOrBlank()) return "http://10.0.2.2:8080/mock/exam"
    val t = u.trim()
    return when {
      t.startsWith("https://") || t.startsWith("http://") -> t
      else -> "https://$t"
    }
  }
  val policy = remember {
    val defaultUrl = normalizeUrl(initialUrl)
    val host = runCatching { android.net.Uri.parse(defaultUrl).host ?: "10.0.2.2" }.getOrDefault("10.0.2.2")
    val root = host.removePrefix("www.")
    val withWww = if (host.startsWith("www.")) host else "www.$host"
    ExamPolicy(
      examId = initialExamId ?: "android-emulator-exam",
      launchUrl = defaultUrl,
      allowedDomains = listOf(host, root, withWww).distinct(),
      blockExternalNavigation = true,
      enableCopyPaste = false,
      enableDownloads = false,
      enableUploads = false,
      heartbeatSeconds = 15,
      teacherPinRequired = false,
      maxBackgroundSeconds = 0,
      violationThreshold = 3,
      managedMode = false
    )
  }
  var showExit by remember { mutableStateOf(false) }
  var pin by remember { mutableStateOf("") }
  var loading by remember { mutableStateOf(true) }
  var errorMsg by remember { mutableStateOf<String?>(null) }
  var launchUrl by remember { mutableStateOf(policy.launchUrl) }
  val violation = rememberViolationTracker()
  val kiosk = rememberKioskController()
  val sessionRepo = rememberSessionRepository()
  val session = remember { SessionManager(violation, kiosk, sessionRepo) }
  LaunchedEffect(Unit) { violation.reset() }
  LaunchedEffect(Unit) {
    val caps = mapOf("webview" to true, "screenshotDetection" to false, "deviceType" to "android")
    if (!initialUrl.isNullOrBlank() && initialExamId.isNullOrBlank()) {
      launchUrl = normalizeUrl(policy.launchUrl).replaceFirst("^http://".toRegex(), "https://")
    } else {
      runCatching {
        delay(800)
        Log.d("EXAMPROTECT", "ExamScreen: calling startSession to backend")
        val resp = sessionRepo.startSession("emu-android-001", policy.examId, "BYOD", "dev-app", caps, "", null)
        launchUrl = resp.launchUrl.replaceFirst("^http://".toRegex(), "https://")
      }
    }
  }
  // auto-exit dinonaktifkan; pelanggaran dipantau tanpa menutup otomatis
  DisposableEffect(Unit) {
    activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    val c = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    c.hide(WindowInsetsCompat.Type.systemBars())
    session.start(activity, policy)
    lifecycleOwner.lifecycle.addObserver(session)
    onDispose {
      c.show(WindowInsetsCompat.Type.systemBars())
      lifecycleOwner.lifecycle.removeObserver(session)
      session.stop(activity)
      activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
    }
  }
  BackHandler(true) {}
  Box(Modifier.fillMaxSize()) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = {
        WebView(it).apply {
          if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true)
          }
          
          isClickable = true
          isLongClickable = false
          setOnLongClickListener { true }
          setBackgroundColor(Color.WHITE)
          settings.javaScriptEnabled = true
          settings.userAgentString = settings.userAgentString + " ExamProtectClient/1.0"
          settings.domStorageEnabled = true
          settings.setSupportMultipleWindows(false)
          settings.displayZoomControls = false
          settings.builtInZoomControls = false
          settings.allowFileAccess = false
          settings.allowContentAccess = false
          settings.cacheMode = WebSettings.LOAD_DEFAULT
          if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
          }
          webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message?): Boolean {
              return false
            }
            override fun onShowFileChooser(webView: WebView?, filePathCallback: android.webkit.ValueCallback<Array<android.net.Uri>>?, fileChooserParams: android.webkit.WebChromeClient.FileChooserParams?): Boolean {
              return policy.enableUploads
            }
          }
          setDownloadListener(DownloadListener { _, _, _, _, _ ->  })
          webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
              if (request == null) return true
              var url = request.url.toString()
              val scheme = request.url.scheme ?: ""
              if (scheme == "http") {
                url = url.replaceFirst("^http://".toRegex(), "https://")
                val headers = mapOf("X-ExamProtect" to "1")
                view?.loadUrl(url, headers)
                return true
              }
              if (scheme in listOf("intent", "market", "tel", "sms", "mailto", "file", "geo", "about", "data", "blob", "content", "chrome")) return true
              if (!isAllowed(url, policy.allowedDomains)) return true
              return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              loading = false
              if (!policy.enableCopyPaste) {
                view?.evaluateJavascript("document.documentElement.style.webkitUserSelect='none';document.documentElement.style.userSelect='none';", null)
              }
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
              errorMsg = error?.description?.toString() ?: "Gagal memuat halaman"
              loading = false
            }
          }
          val headers = mapOf("X-ExamProtect" to "1")
          loadUrl(launchUrl, headers)
        }
      },
      update = { view ->
        val headers = mapOf("X-ExamProtect" to "1")
        view.loadUrl(launchUrl, headers)
      }
    )
    if (loading) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Memuat ujian...")
      }
    }
    errorMsg?.let { msg ->
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $msg")
      }
    }
    Button(
      onClick = { showExit = true },
      modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
    ) {
      Text(if (policy.teacherPinRequired) "Keluar (PIN)" else "Selesai")
    }
  }

  if (showExit) {
    AlertDialog(
      onDismissRequest = { showExit = false },
      title = { Text("Masukkan PIN Pengajar") },
      text = {
        Column {
          Text("Untuk demo, gunakan 000000.")
          OutlinedTextField(value = pin, onValueChange = { pin = it }, singleLine = true, label = { Text("PIN 6 digit") })
        }
      },
      confirmButton = { TextButton(onClick = {
        if (!policy.teacherPinRequired) {
          showExit = false
          onFinish()
        }
      }) { Text("OK") } },
      dismissButton = { TextButton(onClick = { showExit = false }) { Text("Batal") } }
    )
  }
}

private fun isAllowed(url: String, allowedDomains: List<String>): Boolean {
  return runCatching {
    val host = android.net.Uri.parse(url).host ?: return false
    allowedDomains.any { d -> host == d || host.endsWith(".$d") }
  }.getOrDefault(false)
}

@Composable
private fun rememberViolationTracker(): ViolationTracker {
  val context = LocalContext.current
  val entryPoint = EntryPointAccessors.fromApplication(context, ViolationEntryPoint::class.java)
  return entryPoint.tracker()
}

@Composable
private fun rememberKioskController(): KioskController {
  val context = LocalContext.current
  val entryPoint = EntryPointAccessors.fromApplication(context, KioskEntryPoint::class.java)
  return entryPoint.kiosk()
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface ViolationEntryPoint {
  fun tracker(): ViolationTracker
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface KioskEntryPoint {
  fun kiosk(): KioskController
}

@Composable
private fun rememberSessionRepository(): SessionRepository {
  val context = LocalContext.current
  val entryPoint = EntryPointAccessors.fromApplication(context, SessionEntryPoint::class.java)
  return entryPoint.repository()
}
