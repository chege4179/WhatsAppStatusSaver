package com.peterchege.statussaver.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.peterchege.statussaver.core.utils.SingleMediaScanner
import com.peterchege.statussaver.domain.models.SaveResult
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.SavedStatusImagesRepository
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SavedStatusImageRepositoryImpl @Inject constructor(

    private val context: Context,
) : SavedStatusImagesRepository {

    val TAG = SavedStatusImageRepositoryImpl::class.java.simpleName
    override suspend fun saveStatusImage(statusFile: StatusFile):SaveResult<String> {
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
        var fileName =""
        if (statusFile.isVideo) {
            fileName = "VID_$currentDateTime.mp4"
        } else {
            fileName = "IMG_$currentDateTime.jpg"
        }
        val destFile = File(file.toString() + File.separator + fileName)
        return SaveResult.Failure("")
    }

    override fun getSavedStatusImages() {
        TODO(reason = "Not yet implemented")
    }

}