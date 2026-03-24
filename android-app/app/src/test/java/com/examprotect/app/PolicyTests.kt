package com.examprotect.app

import com.examprotect.app.domain.model.ExamPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PolicyTests {
  private fun isAllowed(url: String, allowed: List<String>): Boolean {
    val host = java.net.URI(url).host ?: return false
    return allowed.any { d -> host == d || host.endsWith(".$d") }
  }

  @Test
  fun allowlistWorks() {
    val allowed = listOf("example.com", "forms.gle")
    assertTrue(isAllowed("https://example.com/exam", allowed))
    assertTrue(isAllowed("https://sub.example.com/task", allowed))
    assertFalse(isAllowed("https://evil.com", allowed))
  }
}
