package com.examprotect.app.di

import com.examprotect.app.data.session.SessionRepository
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionEntryPoint {
  fun repository(): SessionRepository
}
