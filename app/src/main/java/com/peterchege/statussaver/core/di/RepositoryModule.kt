/*
 * Copyright 2023 WhatsApp Status Saver
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peterchege.statussaver.core.di

import android.content.Context
import com.peterchege.statussaver.core.firebase.analytics.FirebaseAnalyticsHelper
import com.peterchege.statussaver.core.firebase.crashlytics.FirebaseLogger
import com.peterchege.statussaver.data.SavedStatusRepositoryImpl
import com.peterchege.statussaver.data.StatusImagesRepositoryImpl
import com.peterchege.statussaver.data.StatusVideosRepositoryImpl
import com.peterchege.statussaver.domain.repos.SavedStatusRepository
import com.peterchege.statussaver.domain.repos.StatusImagesRepository
import com.peterchege.statussaver.domain.repos.StatusVideosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideWhatsAppImagesRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        firebaseLogger:FirebaseLogger
    ): StatusImagesRepository {
        return StatusImagesRepositoryImpl(
            ioDispatcher = ioDispatcher,
            appContext = context,
            firebaseLogger = firebaseLogger,
        )
    }

    @Singleton
    @Provides
    fun provideWhatsAppVideosRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        firebaseLogger:FirebaseLogger
    ): StatusVideosRepository {
        return StatusVideosRepositoryImpl(
            ioDispatcher = ioDispatcher,
            appContext = context,
            firebaseLogger = firebaseLogger,
        )
    }

    @Singleton
    @Provides
    fun provideSavedImagesRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        analyticsHelper: FirebaseAnalyticsHelper,
        firebaseLogger:FirebaseLogger
    ): SavedStatusRepository {
        return SavedStatusRepositoryImpl(
            context = context,
            ioDispatcher = ioDispatcher,
            analyticsHelper = analyticsHelper,
            firebaseLogger = firebaseLogger,
        )
    }
}