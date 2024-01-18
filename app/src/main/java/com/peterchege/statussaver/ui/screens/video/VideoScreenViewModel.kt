package com.peterchege.statussaver.ui.screens.video

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.StatusVideosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class VideoScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val player: Player,
    private val videosRepository: StatusVideosRepository,
):ViewModel() {

    val TAG = VideoScreenViewModel::class.java.simpleName

    val videoName = savedStateHandle.getStateFlow(key ="videoName", initialValue = "")

    private val _videos = MutableStateFlow<List<StatusFile>>(emptyList())
    val videos = _videos.asStateFlow()
    fun getPlayer():Player{
        return player
    }
    init {
        loadVideos()
        player.prepare()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            val newVideos = videosRepository.getAllWhatsAppStatusVideos()

            Timber.tag(TAG).i("Videos >>>> ${newVideos}")
            val mediaItems = newVideos?.map { video ->
                val uri = if (video.isApi30) video.documentFile?.uri else video.file?.toUri()
                MediaItem.fromUri(uri!!)
            }
            _videos.update { newVideos ?: it }

            if (mediaItems != null) {
                player.addMediaItems(mediaItems)
            }
        }
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
    }

    fun findStatusFileByName(videoName: String): StatusFile? {
        return _videos.value.find { it.title == videoName }
    }



}