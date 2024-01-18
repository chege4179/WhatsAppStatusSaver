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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.components.FullScreenVideo
import com.peterchege.statussaver.ui.components.ImageCard
import com.peterchege.statussaver.ui.components.VideoCard

@Composable
fun AllVideosScreen(
    viewModel: AllVideosScreenViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()



    AllVideosScreenContent(
        videos = videos,
        activeVideo = activeVideo,
        onChangeActiveVideo = viewModel::onChangeActivePhoto,
        player = viewModel.getPlayer()
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVideosScreenContent(
    videos: List<StatusFile>,
    activeVideo: StatusFile?,
    onChangeActiveVideo: (StatusFile?) -> Unit,
    player: Player,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Videos")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val gridState = rememberLazyGridState()
            if (videos.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(128.dp),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(items = videos, key = { videos.indexOf(it) }) {
                        VideoCard(
                            onSaveVideo = {
                                onChangeActiveVideo(it)
                            },
                            video = it
                        )
                    }
                }
            } else {
                Text("No files found")
            }
        }
        if (activeVideo != null) {
            FullScreenVideo(
                photo = activeVideo,
                onDismiss = { onChangeActiveVideo(null) },
                player = player,
            )
        }
    }
}