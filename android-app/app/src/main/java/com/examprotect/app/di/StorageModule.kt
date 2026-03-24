package com.examprotect.app.di

import android.content.Context
import androidx.room.Room
import com.examprotect.app.data.violation.ViolationDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
  @Provides
  @Singleton
  fun provideDb(@ApplicationContext ctx: Context): ViolationDb =
    Room.databaseBuilder(ctx, ViolationDb::class.java, "violations.db")
      .fallbackToDestructiveMigration()
      .build()
}
