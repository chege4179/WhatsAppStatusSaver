package com.peterchege.statussaver.domain.repos

import com.peterchege.statussaver.domain.models.StatusFile

interface StatusVideosRepository {

    suspend fun getAllWhatsAppStatusVideos():List<StatusFile>?
}