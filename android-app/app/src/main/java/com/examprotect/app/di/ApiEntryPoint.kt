package com.examprotect.app.di

import com.examprotect.app.data.network.ApiService
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface ApiEntryPoint {
  fun api(): ApiService
}
