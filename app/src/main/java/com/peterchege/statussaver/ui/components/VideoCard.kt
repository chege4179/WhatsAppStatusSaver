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
package com.peterchege.statussaver.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.R

@Composable
fun VideoCard(
    isSaved: Boolean,
    video: StatusFile,
    saveVideo: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
    setActiveVideo: (StatusFile) -> Unit,
) {
    val context = LocalContext.current
    val whatsappUri = if (video.isApi30) video.documentFile?.uri else video.file?.toUri()
    val savedUri = video.file
    val uri = if (isSaved) savedUri else whatsappUri
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = uri,
        imageLoader = imageLoader,
    )

    Column(
        modifier = Modifier
            .height(170.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.LightGray)
    ) {
        Image(
            painter = painter,
            modifier = Modifier
                .height(128.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(7.dp))
                .pointerInput(setActiveVideo) {
                    detectTapGestures { setActiveVideo(video) }
                },
            contentScale = ContentScale.FillWidth,
            contentDescription = stringResource(id = R.string.whatsapp_video)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isSaved) {
                CustomIconButton(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(id = R.string.download_video_description),
                    onClick = { saveVideo(video) }
                )
            }

            CustomIconButton(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(id = R.string.share_video_description),
                onClick = { shareVideo(video) }
            )
        }
    }
}