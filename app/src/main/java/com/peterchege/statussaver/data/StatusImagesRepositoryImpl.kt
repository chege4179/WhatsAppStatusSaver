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
package com.peterchege.statussaver.data

import android.content.Context
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.peterchege.statussaver.core.di.IoDispatcher
import com.peterchege.statussaver.core.firebase.crashlytics.FirebaseLogger
import com.peterchege.statussaver.core.utils.Constants.STATUS_DIRECTORY
import com.peterchege.statussaver.core.utils.isVideo
import com.peterchege.statussaver.core.utils.sdk29AndUp
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.StatusImagesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject


//  WhatsApp/Media/.Statuses
class StatusImagesRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val appContext: Context,
    private val firebaseLogger: FirebaseLogger,
) : StatusImagesRepository {


    val TAG = StatusVideosRepositoryImpl::class.java.simpleName

    override suspend fun getAllWhatsAppStatusImages(): List<StatusFile> {
        return withContext(ioDispatcher) {
            sdk29AndUp(
                onSdk29 = { getWhatsAppStatusImagesGreaterThan29() },
                onBelowSdk29 = { getWhatsAppStatusImagesLessThanAPI29() }
            )
        }
    }

    private fun getWhatsAppStatusImagesLessThanAPI29(): List<StatusFile> {
        val statusFiles = STATUS_DIRECTORY.listFiles()?.toList()
        val images = statusFiles?.filter { !it.isVideo() && it.name.endsWith(".jpg") }
            ?.filterNot { it.name.contains(".nomedia") }
        return images?.map {
            StatusFile(
                file = it,
                title = it.name,
                path = it.path,
                isApi30 = false,
                isVideo = it.isVideo(),
                documentFile = null
            )
        } ?: emptyList()
    }

    private fun getWhatsAppStatusImagesGreaterThan29(): List<StatusFile> {
        val list = appContext.contentResolver.persistedUriPermissions;
        Timber.tag(TAG).i("Permissions List Count ${list.size}")
        Timber.tag(TAG).i("Permissions List $list")
        try {
            val file = DocumentFile.fromTreeUri(appContext, list[0].uri) ?: return emptyList()
            val statusFiles = file.listFiles()
            return statusFiles
                .filterNot { it.name!!.contains(".nomedia") }
                .map {
                    StatusFile(
                        documentFile = it,
                        isApi30 = true,
                        isVideo = it.name?.endsWith(".mp4") ?: false,
                        title = it.name ?: "",
                        path = null,
                        file = null
                    )
                }
                .filter { !it.isVideo }
        }catch (e:Exception){
            e.printStackTrace()
            firebaseLogger.logException(e)
            return emptyList()

        }

    }

}