/*
 * Copyright 2024 WhatsApp Status Saver
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

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.peterchege.statussaver.core.firebase.analytics.AnalyticsHelper
import com.peterchege.statussaver.core.firebase.analytics.FirebaseAnalyticsHelper
import com.peterchege.statussaver.core.firebase.crashlytics.FirebaseLogger
import com.peterchege.statussaver.core.firebase.crashlytics.FirebaseLoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule  {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return Firebase.crashlytics
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalyticsHelper(firebaseAnalytics: FirebaseAnalytics): AnalyticsHelper {
        return FirebaseAnalyticsHelper(firebaseAnalytics = firebaseAnalytics)

    }

    @Provides
    @Singleton
    fun provideFirebaseLogger(firebaseCrashlytics: FirebaseCrashlytics): FirebaseLogger {
        return FirebaseLoggerImpl(firebaseCrashlytics = firebaseCrashlytics)

    }
}