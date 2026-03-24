package com.examprotect.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfigScreen(onConfigured: (String, String) -> Unit) {
  var url by remember { mutableStateOf("") }
  var examId by remember { mutableStateOf("sample-exam-001") }
  fun normalize(u: String): String {
    val t = u.trim()
    if (t.isEmpty()) return t
    return if (t.startsWith("http://") || t.startsWith("https://")) t else "https://$t"
  }
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Center
  ) {
    Text("Tempel link ujian (opsional jika pakai examId lokal)")
    OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Launch URL (opsional)") })
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(value = examId, onValueChange = { examId = it }, label = { Text("Exam ID") })
    Spacer(Modifier.height(12.dp))
    Button(onClick = { onConfigured(normalize(url), examId.trim()) }) { Text("Lanjut") }
  }
}
