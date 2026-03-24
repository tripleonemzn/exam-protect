package com.examprotect.app.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
  @GET("/v1/client/update")
  suspend fun getUpdate(): UpdateInfo

  @GET("/v1/exams/{examId}/config")
  suspend fun getExamConfig(@Path("examId") examId: String, @Query("ssoToken") ssoToken: String? = null): SignedConfigResponse

  @POST("/v1/sessions/start")
  suspend fun startSession(@Body body: SessionStartRequest): SessionStartResponse

  @POST("/v1/sessions/{sessionId}/heartbeat")
  suspend fun heartbeat(@Path("sessionId") sessionId: String, @Body body: HeartbeatRequest): HeartbeatResponse

  @POST("/v1/sessions/{sessionId}/violations")
  suspend fun reportViolation(@Path("sessionId") sessionId: String, @Body body: ViolationReport)

  @POST("/v1/sessions/{sessionId}/complete")
  suspend fun complete(@Path("sessionId") sessionId: String, @Body body: CompleteRequest)
}
