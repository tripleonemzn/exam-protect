package com.examprotect.app.data.network

import com.examprotect.app.domain.model.Branding
import com.examprotect.app.domain.model.ExamPolicy

data class SignedConfigResponse(
  val examId: String,
  val jws: String
)

data class SessionStartRequest(
  val deviceId: String,
  val examId: String,
  val mode: String,
  val appVersion: String,
  val capabilities: Map<String, Any?>,
  val policyHash: String,
  val entryPin: String?
)

data class SessionStartResponse(
  val sessionId: String,
  val heartbeatSeconds: Int,
  val serverTime: String,
  val launchUrl: String
)

data class UpdateInfo(
  val versionCode: Int,
  val versionName: String,
  val mandatory: Boolean,
  val notes: String?,
  val downloadUrl: String?
)

data class HeartbeatRequest(
  val timestamp: String,
  val violations: Int,
  val events: List<Map<String, Any?>>?,
  val metrics: Map<String, Any?>?,
  val nonce: String
)

data class HeartbeatResponse(
  val status: String,
  val action: String,
  val serverTime: String
)

data class ViolationReport(
  val type: String,
  val detail: String?,
  val ts: String
)

data class CompleteRequest(
  val endedReason: String,
  val violations: Int,
  val summary: Map<String, Any?>
)

data class PolicyPayload(
  val version: Int?,
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
  val branding: Branding?
)

fun PolicyPayload.toDomain(): ExamPolicy = ExamPolicy(
  examId = examId,
  launchUrl = launchUrl,
  allowedDomains = allowedDomains,
  blockExternalNavigation = blockExternalNavigation,
  enableCopyPaste = enableCopyPaste,
  enableDownloads = enableDownloads,
  enableUploads = enableUploads,
  heartbeatSeconds = heartbeatSeconds,
  teacherPinRequired = teacherPinRequired,
  maxBackgroundSeconds = maxBackgroundSeconds,
  violationThreshold = violationThreshold,
  managedMode = managedMode,
  branding = branding
)
