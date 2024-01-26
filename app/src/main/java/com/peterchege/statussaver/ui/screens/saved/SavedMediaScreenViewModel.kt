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
package com.peterchege.statussaver.ui.screens.saved

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.SavedStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SavedMediaScreenViewModel @Inject constructor(
    private val savedStatusRepository: SavedStatusRepository,
    private val player: Player,
):ViewModel(){

    private val _statusFiles = MutableStateFlow<List<StatusFile>>(emptyList())
    val statusFiles = _statusFiles.asStateFlow()

    private val _activePhoto = MutableStateFlow<StatusFile?>(null)
    val activePhoto = _activePhoto.asStateFlow()

    private val _activeVideo = MutableStateFlow<StatusFile?>(null)
    val activeVideo = _activeVideo.asStateFlow()

    init {
        getStatusFiles()
    }

    fun onChangeActivePhoto(photo:StatusFile?){
        _activePhoto.update { photo }
    }


    fun onChangeActiveVideo(statusFile: StatusFile?) {
        _activeVideo.update { statusFile }
        statusFile?.file?.toUri()?.let { playVideo(it) }
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
    }

    fun getPlayer():Player{
        return player
    }

    fun stopPlayer(){
        player.stop()
        player.release()
    }

    private fun getStatusFiles(){
        viewModelScope.launch {
            val savedStatusFiles = savedStatusRepository.getSavedStatus()
            _statusFiles.update { savedStatusFiles }
        }
    }

}