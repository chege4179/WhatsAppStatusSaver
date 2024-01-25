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
package com.peterchege.statussaver.ui.screens.photos

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.components.FullScreenPhoto
import com.peterchege.statussaver.ui.components.ImageCard
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AllPhotosScreen(
    viewModel: AllPhotosScreenViewModel = hiltViewModel(),
    shareImage:(StatusFile) -> Unit,
) {
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val activePhoto by viewModel.activePhoto.collectAsStateWithLifecycle()
    val activity = (LocalContext.current as? Activity)
    BackHandler {
        if (activePhoto != null){
            viewModel.onChangeActivePhoto(null)
        }else{
            activity?.finish()
        }
    }
    AllPhotosScreenContent(
        photos = photos,
        activePhoto = activePhoto,
        onChangeActiveStatusFile = viewModel::onChangeActivePhoto,
        eventFlow = viewModel.eventFlow,
        saveImage = viewModel::savePhoto,
        shareImage = shareImage,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPhotosScreenContent(
    photos: List<StatusFile>,
    activePhoto: StatusFile?,
    onChangeActiveStatusFile: (StatusFile?) -> Unit,
    eventFlow: SharedFlow<String>,
    saveImage:(StatusFile) -> Unit,
    shareImage: (StatusFile) -> Unit,

    ) {
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true){
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
                    Text(text = "WhatsApp Status Saver")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (photos.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(items = photos, key = { photos.indexOf(it) }) {
                        ImageCard(
                            saveImage = saveImage,
                            image = it,
                            isSaved = false,
                            shareImage = shareImage,
                            setActiveImage = onChangeActiveStatusFile
                        )
                    }
                }
            } else {
                Text("No files found")
            }
        }
        if (activePhoto != null) {
            FullScreenPhoto(
                photo = activePhoto,
                onDismiss = { onChangeActiveStatusFile(null) },
                saveImage = saveImage
            )
        }
    }
}