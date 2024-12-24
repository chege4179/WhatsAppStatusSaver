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
package com.peterchege.statussaver.ui.screens.saved

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.Indicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.peterchege.statussaver.R
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.components.AdmobBanner
import com.peterchege.statussaver.ui.screens.saved.photos.SavedPhotosScreen
import com.peterchege.statussaver.ui.screens.saved.videos.SavedVideosScreen
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SavedMediaScreen(
    viewModel: SavedMediaScreenViewModel = hiltViewModel(),
    shareImage: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
) {
    val TAG = "SavedMediaScreen"
    LaunchedEffect(key1 = Unit) {
        viewModel.getStatusFiles()
    }
    val statusFiles by viewModel.statusFiles.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()
    val activePhoto by viewModel.activePhoto.collectAsStateWithLifecycle()

    val activity = (LocalContext.current as? Activity)

    LaunchedEffect(statusFiles) {

        Timber.tag(TAG).i("Status Files $statusFiles")
    }
    BackHandler {
        if (activeVideo != null){
            viewModel.stopPlayer()
            viewModel.onChangeActiveVideo(null)
        }else if (activePhoto !=null){
            viewModel.onChangeActivePhoto(null)
        }else{
            activity?.finish()
        }
    }



    SavedMediaScreenContent(
        statusFiles = statusFiles,
        shareImage = shareImage,
        shareVideo = shareVideo,
        activeVideo = activeVideo,
        activePhoto = activePhoto,
        onChangeActiveVideo = viewModel::onChangeActiveVideo,
        onChangeActivePhoto = viewModel::onChangeActivePhoto,
        player = viewModel.getPlayer()
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedMediaScreenContent(
    statusFiles: List<StatusFile>,
    shareImage: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
    activeVideo:StatusFile?,
    activePhoto:StatusFile?,
    onChangeActiveVideo:(StatusFile?) -> Unit,
    onChangeActivePhoto:(StatusFile?) ->Unit,
    player: Player,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.saved_status))
                }
            )
        },
    ) { paddingValues ->
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { 2 }
        )
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(paddingValues)
                .padding(vertical = 5.dp),
        ) {
            val list = listOf(
                stringResource(id = R.string.images),
                stringResource(id = R.string.videos)
            )
            val scope = rememberCoroutineScope()

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                indicator = { tabPositions ->
                    Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.5.dp
                    )
                }
            ) {
                list.forEachIndexed { index, _ ->
                    Tab(
                        text = {
                            Text(
                                text = list[index],
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary else Color.LightGray
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
            val savedPhotos = remember(statusFiles) {
                statusFiles.filter { !it.isVideo }
            }
            val savedVideos = remember(statusFiles) {
                statusFiles.filter { it.isVideo }
            }
            AdmobBanner(modifier = Modifier.fillMaxWidth())
            HorizontalPager(state = pagerState) {
                AnimatedContent(
                    targetState = pagerState,
                    label = "Horizontal Pager",
                    ) { pager ->

                    when (pager.currentPage) {
                        0 -> {
                            SavedPhotosScreen(
                                statusFiles = savedPhotos,
                                saveImage = { },
                                shareImage = shareImage,
                                activePhoto = activePhoto,
                                onChangeActivePhoto = onChangeActivePhoto
                            )
                        }

                        1 -> {
                            SavedVideosScreen(
                                statusFiles = savedVideos,
                                saveVideo = { },
                                shareVideo = shareVideo,
                                onChangeActiveVideo = onChangeActiveVideo,
                                activeVideo = activeVideo,
                                player = player
                            )
                        }

                    }
                }

            }
        }
    }
}
