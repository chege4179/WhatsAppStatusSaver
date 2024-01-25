package com.peterchege.statussaver.data

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.peterchege.statussaver.core.di.IoDispatcher
import com.peterchege.statussaver.core.utils.Constants.STATUS_DIRECTORY
import com.peterchege.statussaver.core.utils.isVideo
import com.peterchege.statussaver.core.utils.sdk29AndUp
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.StatusVideosRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class StatusVideosRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val appContext: Context,
) : StatusVideosRepository {

    override suspend fun getAllWhatsAppStatusVideos(): List<StatusFile> {
        return sdk29AndUp { getWhatsAppStatusVideosGreaterThan29() }
            ?: getWhatsAppStatusVideosLessThanAPI29()
    }


    private fun getWhatsAppStatusVideosLessThanAPI29(): List<StatusFile> {
        val statusFiles = STATUS_DIRECTORY.listFiles()?.toList()
        val images = statusFiles?.filter { it.isVideo() }
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

    private fun getWhatsAppStatusVideosGreaterThan29(): List<StatusFile> {
        val list = appContext.contentResolver.persistedUriPermissions;
        val file = DocumentFile.fromTreeUri(appContext, list[0].uri) ?: return emptyList()
        val statusFiles = file.listFiles()
        return statusFiles
            .filterNot { it.name!!.contains(".nomedia") }
            .map {
                StatusFile(
                    documentFile = it,
                    isApi30 = true,
                    isVideo = it.name?.endsWith(".mp4") ?: false,
                    title = it.name ?:"",
                    path = null,
                    file = null
                )
            }
    }
}