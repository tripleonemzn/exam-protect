package com.examprotect.app

import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.examprotect.app.ui.screens.ConfigScreen
import com.examprotect.app.ui.screens.ExamScreen
import com.examprotect.app.ui.screens.SplashScreen
import com.examprotect.app.ui.screens.SummaryScreen
import com.examprotect.app.ui.screens.ChecklistScreen
import com.examprotect.app.ui.screens.GateScreen
import com.examprotect.app.update.UpdateManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.decorView.filterTouchesWhenObscured = true
    setContent {
      App()
    }
  }
}

@Composable
private fun App() {
  val nav = rememberNavController()
  val ctx = LocalContext.current
  LaunchedEffect(Unit) {
    UpdateManager.checkAndNotify(ctx)
  }
  Surface(color = MaterialTheme.colorScheme.background) {
    NavHost(navController = nav, startDestination = "gate") {
      composable("gate") { GateScreen(onAccessGranted = { nav.navigate("config") { popUpTo("gate") { inclusive = true } } }) }
      composable("splash") { SplashScreen(onDone = { nav.navigate("config") { popUpTo("splash") { inclusive = true } } }) }
      composable("config") { ConfigScreen(onConfigured = { url, examId ->
        val u = java.net.URLEncoder.encode(url, "UTF-8")
        val e = java.net.URLEncoder.encode(examId, "UTF-8")
        val route = if (url.isNotBlank()) "exam?url=$u&eid=" else "exam?url=&eid=$e"
        nav.navigate(route)
      }) }
      composable(
        route = "exam?url={url}&eid={eid}",
        arguments = listOf(
          navArgument("url") { type = NavType.StringType; defaultValue = "" },
          navArgument("eid") { type = NavType.StringType; defaultValue = "" }
        )
      ) { backStackEntry ->
        val urlArg = backStackEntry.arguments?.getString("url")?.takeIf { it.isNotBlank() }
        val examArg = backStackEntry.arguments?.getString("eid")?.takeIf { it.isNotBlank() }
        ExamScreen(onFinish = { nav.navigate("summary") }, initialUrl = urlArg, initialExamId = examArg)
      }
      composable("summary") { SummaryScreen(onExit = { (ctx as? ComponentActivity)?.finishAffinity() }) }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.antiTapjacking(): Modifier = this.pointerInteropFilter {
  val obscured = it.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED != 0
  !obscured
}
