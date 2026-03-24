package com.examprotect.app.di

import com.examprotect.app.data.violation.ViolationDb
import com.examprotect.app.violation.ViolationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViolationModule {
  @Provides
  @Singleton
  fun provideViolationTracker(db: ViolationDb): ViolationTracker = ViolationTracker(db.dao())
}
