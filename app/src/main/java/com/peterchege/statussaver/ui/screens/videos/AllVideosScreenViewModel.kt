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
package com.peterchege.statussaver.ui.screens.videos

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.StatusVideosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllVideosScreenViewModel @Inject constructor(
    private val videosRepository: StatusVideosRepository,
    private val player: Player,
) : ViewModel() {

    val TAG = AllVideosScreenViewModel::class.java.simpleName

    private val _videos = MutableStateFlow<List<StatusFile>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _activeVideo = MutableStateFlow<StatusFile?>(null)
    val activeVideo = _activeVideo.asStateFlow()

    init {
        loadVideos()
        player.prepare()
    }

    fun getPlayer():Player{
        return player
    }

    private fun loadVideos() {
        viewModelScope.launch {
            val newVideos = videosRepository.getAllWhatsAppStatusVideos()

            Timber.tag(TAG).i("Videos >>>> ${newVideos}")
            _videos.update { newVideos ?: it }
            val mediaItems = newVideos?.map { video ->
                val uri = if (video.isApi30) video.documentFile?.uri else video.file?.toUri()
                MediaItem.fromUri(uri!!)
            }
            if (mediaItems != null) {
                player.addMediaItems(mediaItems)
            }
        }
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
    }

    fun onChangeActivePhoto(video: StatusFile?) {
        _activeVideo.update { video }
        if (video !=null){
            val uri = if (video.isApi30) video.documentFile?.uri else video.file?.toUri()
            if (uri != null) {
                playVideo(uri = uri)
            }
        }
    }


}