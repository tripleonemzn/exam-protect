package com.examprotect.app.di

import com.examprotect.app.BuildConfig
import com.examprotect.app.data.network.ApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  @Singleton
  fun provideMoshi(): Moshi = Moshi.Builder().build()

  @Provides
  @Singleton
  fun provideOkHttp(): OkHttpClient {
    val builder = OkHttpClient.Builder()
    if (BuildConfig.ENABLE_HTTP_LOGGING) {
      builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    }
    val baseHost = runCatching { java.net.URI(BuildConfig.BACKEND_BASE_URL).host }.getOrNull()
    if (!baseHost.isNullOrBlank()) {
      val pinner = CertificatePinner.Builder().build()
      builder.certificatePinner(pinner)
    }
    return builder.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(ok: OkHttpClient, moshi: Moshi): Retrofit {
    var base = BuildConfig.BACKEND_BASE_URL.ifBlank { "https://backend.invalid/" }
    if (!base.endsWith("/")) base += "/"
    return Retrofit.Builder()
      .baseUrl(base)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(ok)
      .build()
  }

  @Provides
  @Singleton
  fun provideApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
