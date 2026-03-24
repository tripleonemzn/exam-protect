package com.examprotect.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummaryScreen(onExit: () -> Unit) {
  Column(Modifier.fillMaxSize().padding(16.dp)) {
    Text("Ringkasan pelanggaran akan ditampilkan di sini.")
    Button(onClick = onExit) { Text("Keluar") }
  }
}
