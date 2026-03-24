package com.examprotect.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.examprotect.app.security.EnvironmentChecks

@Composable
fun ChecklistScreen(onProceed: () -> Unit) {
  val dbg = EnvironmentChecks.isDebuggerAttached()
  val emu = EnvironmentChecks.isProbablyEmulator()
  val root = EnvironmentChecks.isRootSuspected()
  Column(Modifier.fillMaxSize().padding(16.dp)) {
    Text("Pre-exam checklist:")
    Text("• Debugger terdeteksi: $dbg")
    Text("• Emulator terdeteksi: $emu")
    Text("• Root terindikasi: $root")
    Spacer(Modifier.height(12.dp))
    Button(onClick = onProceed) { Text("Mulai Ujian") }
  }
}
