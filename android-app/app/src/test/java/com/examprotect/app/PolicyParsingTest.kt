package com.examprotect.app

import com.examprotect.app.data.network.PolicyPayload
import com.examprotect.app.data.network.toDomain
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class PolicyParsingTest {
  @Test
  fun parseSamplePolicy() {
    val json = """
      {
        "examId":"sample-exam-001",
        "launchUrl":"https://example.com/exam",
        "allowedDomains":["example.com","docs.google.com","forms.gle","accounts.google.com"],
        "blockExternalNavigation":true,
        "enableCopyPaste":false,
        "enableDownloads":false,
        "enableUploads":false,
        "heartbeatSeconds":15,
        "teacherPinRequired":true,
        "maxBackgroundSeconds":3,
        "violationThreshold":3,
        "managedMode":false,
        "branding":{"appName":"EXAM-PROTECT"}
      }
    """.trimIndent()
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val payload = moshi.adapter(PolicyPayload::class.java).fromJson(json)!!
    val p = payload.toDomain()
    assertEquals("sample-exam-001", p.examId)
    assertEquals(false, p.enableDownloads)
    assertEquals(true, p.teacherPinRequired)
  }
}
