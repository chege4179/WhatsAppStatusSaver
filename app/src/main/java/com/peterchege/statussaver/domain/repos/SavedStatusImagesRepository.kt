package com.peterchege.statussaver.domain.repos

import com.peterchege.statussaver.domain.models.SaveResult
import com.peterchege.statussaver.domain.models.StatusFile

interface SavedStatusImagesRepository {

    suspend fun saveStatusImage(statusFile: StatusFile):SaveResult<String>

    fun getSavedStatusImages()
}