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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peterchege.statussaver.R
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.screens.saved.photos.SavedPhotosScreen
import com.peterchege.statussaver.ui.screens.saved.videos.SavedVideosScreen
import kotlinx.coroutines.launch

@Composable
fun SavedMediaScreen(
    viewModel: SavedMediaScreenViewModel = hiltViewModel(),
    shareImage: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
) {
    val statusFiles by viewModel.statusFiles.collectAsStateWithLifecycle()


    SavedMediaScreenContent(
        statusFiles = statusFiles,
        shareImage = shareImage,
        shareVideo = shareVideo,
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedMediaScreenContent(
    statusFiles: List<StatusFile>,
    shareImage: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
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
                    Text(text = "Saved Statuses")
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
            HorizontalPager(state = pagerState) {
                AnimatedContent(
                    targetState = pagerState,
                    label = "Horizontal Pager",

                    ) { pager ->
                    when (pager.currentPage) {
                        0 -> SavedPhotosScreen(
                            statusFiles = statusFiles.filter { !it.isVideo },
                            saveImage = { },
                            shareImage = shareImage
                        )

                        1 -> SavedVideosScreen(
                            statusFiles = statusFiles.filter { it.isVideo },
                            saveVideo = { },
                            shareVideo = shareVideo
                        )

                    }
                }

            }
        }
    }
}
