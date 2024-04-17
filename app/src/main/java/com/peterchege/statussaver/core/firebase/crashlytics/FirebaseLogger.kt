package com.peterchege.statussaver.core.firebase.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

interface FirebaseLogger {
    fun logException(e:Exception)

}


class FirebaseLoggerImpl @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics
):FirebaseLogger{

    override fun logException(e: Exception) {
        firebaseCrashlytics.recordException(e)
    }
}