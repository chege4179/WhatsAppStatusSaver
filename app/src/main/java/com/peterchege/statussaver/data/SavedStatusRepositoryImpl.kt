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
package com.peterchege.statussaver.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.peterchege.statussaver.core.di.IoDispatcher
import com.peterchege.statussaver.core.utils.SingleMediaScanner
import com.peterchege.statussaver.core.utils.isVideo
import com.peterchege.statussaver.core.utils.sdk29AndUp
import com.peterchege.statussaver.domain.models.SaveResult
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.SavedStatusRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SavedStatusRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
) : SavedStatusRepository {

    val TAG = SavedStatusRepositoryImpl::class.java.simpleName

    override suspend fun getSavedStatus():List<StatusFile> {
        return withContext(ioDispatcher){
            val APP_DIR = context.getExternalFilesDir("StatusDownloader")?.path
                ?: return@withContext emptyList()
            val app_dir = File(APP_DIR)
            Timber.tag(TAG).i("File saved >>> $app_dir")
            if (!app_dir.exists()) {
                if (!app_dir.mkdirs()) {
                    return@withContext emptyList()
                }
            }
            val savedFiles = sdk29AndUp(
                onSdk29 = {
                    val f = File(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                        ).toString() + File.separator + "status_saver"
                    )
                    f.listFiles()
                },
                onBelowSdk29 = {
                    app_dir.listFiles()
                }
            )?.toList()
            if (savedFiles != null){
                val savedStatusFiles = savedFiles.map {
                    StatusFile(
                        file = it,
                        title = it.name ?: "",
                        path = it.path,
                        isVideo = it.isVideo(),
                        documentFile = null,
                        isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    )
                }
                return@withContext savedStatusFiles
            }else{
                return@withContext emptyList()
            }
        }

    }

    override suspend fun saveStatus(statusFile: StatusFile): SaveResult<String> {
        val APP_DIR = context.getExternalFilesDir("StatusDownloader")?.path
            ?: return SaveResult.Failure(msg = "Directory not found")
        val file = File(APP_DIR)
        Timber.tag(TAG).i("File saved >>> $file")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return SaveResult.Failure(msg = "Directory not created ")
            }
        }
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateTime = sdf.format(Date())
        var fileName = ""
        if (statusFile.isVideo) {
            fileName = "VID_$currentDateTime.mp4"
        } else {
            fileName = "IMG_$currentDateTime.jpg"
        }
        val destFile = File(file.toString() + File.separator + fileName)

        return try {
            sdk29AndUp(
                onSdk29 = {
                    val values = ContentValues()

                    val destinationUri: Uri?

                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    values.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DCIM + "/status_saver"
                    )

                    val collectionUri: Uri
                    collectionUri = if (statusFile.isVideo) {
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/*")
                        MediaStore.Video.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY
                        )
                    } else {
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY
                        )
                    }

                    destinationUri = context.contentResolver.insert(collectionUri, values)

                    val inputStream =
                        context.contentResolver.openInputStream(statusFile.documentFile!!.uri)
                    val outputStream = context.contentResolver.openOutputStream(
                        destinationUri!!
                    )
                    IOUtils.copy(inputStream, outputStream)
                    if (statusFile.isVideo){
                        SaveResult.Success(msg = "Video Saved successfully")
                    }else{
                        SaveResult.Success(msg = "Image Saved successfully")
                    }
                },
                onBelowSdk29 = {
                    FileUtils.copyFile(statusFile.file, destFile)
                    //noinspection ResultOfMethodCallIgnored
                    destFile.setLastModified(System.currentTimeMillis())
                    SingleMediaScanner(context, file)

                    val data = FileProvider.getUriForFile(
                        context, "com.peterchege.statussaver.provider",
                        File(destFile.absolutePath)
                    )
                    if (statusFile.isVideo){
                        SaveResult.Success(msg = "Video Saved successfully")
                    }else{
                        SaveResult.Success(msg = "Image Saved successfully")
                    }

                }
            )
        } catch (e: IOException) {
            e.printStackTrace();
            SaveResult.Failure(msg = e.message ?: "Error saving file")
        }
    }



}