package com.examprotect.app.di

import android.content.Context
import com.examprotect.app.kiosk.KioskController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KioskModule {
  @Provides
  @Singleton
  fun provideKiosk(@ApplicationContext ctx: Context): KioskController = KioskController(ctx)
}
