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

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.peterchege.statussaver.R
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.components.AdmobBanner
import com.peterchege.statussaver.ui.components.AppLoader
import com.peterchege.statussaver.ui.components.FullScreenVideo
import com.peterchege.statussaver.ui.components.VideoCard
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AllVideosScreen(
    viewModel: AllVideosScreenViewModel = hiltViewModel(),
    shareVideo: (StatusFile) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val player = viewModel.getPlayer()

    val activity = (LocalContext.current as? Activity)
    BackHandler(enabled = uiState.activeVideo != null) {
        if (uiState.activeVideo != null) {
            viewModel.stopPlayer()
            viewModel.onChangeActiveVideo(null)
        } else {
            activity?.finish()
        }
    }

    AppLoader(isLoading = uiState.isLoading)
    AllVideosScreenContent(
        videos =uiState.videos,
        activeVideo = uiState.activeVideo,
        player = player,
        onChangeActiveVideo = viewModel::onChangeActiveVideo,
        saveStatus = viewModel::saveVideo,
        eventFlow = viewModel.eventFlow,
        shareVideo = shareVideo,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVideosScreenContent(
    videos: List<StatusFile>,
    shareVideo: (StatusFile) -> Unit,
    onChangeActiveVideo: (StatusFile?) -> Unit,
    activeVideo: StatusFile?,
    saveStatus: (StatusFile) -> Unit,
    player: Player,
    eventFlow: SharedFlow<String>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        eventFlow.collectLatest {
            snackbarHostState.showSnackbar(message = it)
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.videos))
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
            AdmobBanner(modifier = Modifier.fillMaxWidth())
            AnimatedContent(targetState = videos.isNotEmpty(), label = "Videos") {
                if (it) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(128.dp),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(items = videos) {
                            VideoCard(
                                video = it,
                                isSaved = false,
                                shareVideo = shareVideo,
                                saveVideo = saveStatus,
                                setActiveVideo = onChangeActiveVideo
                            )
                        }
                    }
                } else {
                    Text(text = stringResource(id = R.string.no_saved_videos_found))
                }
            }

        }
        if (activeVideo != null) {
            FullScreenVideo(
                photo = activeVideo,
                player = player,
                onDismiss = { onChangeActiveVideo(null) },
                onSave = saveStatus
            )
        }
    }
}