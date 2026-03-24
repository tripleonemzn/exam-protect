package com.examprotect.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import com.examprotect.app.di.SessionEntryPoint
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun GateScreen(onAccessGranted: () -> Unit) {
  var pin by remember { mutableStateOf("") }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Center
  ) {
    Text("Masukkan PIN untuk masuk")
    OutlinedTextField(
      value = pin,
      onValueChange = { v -> pin = v.filter { it.isDigit() }.take(6) },
      label = { Text("PIN 6 digit") },
      visualTransformation = PasswordVisualTransformation()
    )
    Button(
      onClick = {
        if (pin == "123456") {
          val ep = EntryPointAccessors.fromApplication(context, SessionEntryPoint::class.java)
          scope.launch { ep.repository().setEntryPin(pin) }
          onAccessGranted()
        }
      },
      enabled = pin.length == 6
    ) { Text("Masuk") }
  }
}
