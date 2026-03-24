package com.examprotect.app.domain.model

data class Branding(
  val appName: String? = null,
  val primaryColor: String? = null,
  val logoUrl: String? = null
)

data class ExamPolicy(
  val examId: String,
  val launchUrl: String,
  val allowedDomains: List<String>,
  val blockExternalNavigation: Boolean,
  val enableCopyPaste: Boolean,
  val enableDownloads: Boolean,
  val enableUploads: Boolean,
  val heartbeatSeconds: Int,
  val teacherPinRequired: Boolean,
  val maxBackgroundSeconds: Int,
  val violationThreshold: Int,
  val managedMode: Boolean,
  val branding: Branding? = null
)
