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
package com.peterchege.statussaver.ui.screens.saved.videos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.components.VideoCard
import com.peterchege.statussaver.R
import com.peterchege.statussaver.ui.components.FullScreenVideo

@Composable
fun SavedVideosScreen(
    statusFiles: List<StatusFile>,
    shareVideo:(StatusFile) -> Unit,
    saveVideo:(StatusFile) -> Unit,
    activeVideo:StatusFile?,
    player: Player,
    onChangeActiveVideo:(StatusFile?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val gridState = rememberLazyGridState()
        if (statusFiles.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(items = statusFiles) {
                    VideoCard(
                        video = it,
                        saveVideo = saveVideo,
                        shareVideo = shareVideo,
                        setActiveVideo = onChangeActiveVideo,
                        isSaved = true
                    )
                }
            }
        } else {
            Text(text = stringResource(id = R.string.no_saved_videos_found))
        }
    }
    if (activeVideo != null) {
        FullScreenVideo(
            photo = activeVideo,
            player = player,
            onDismiss = { onChangeActiveVideo(null) },
            onSave = {  }
        )
    }

}