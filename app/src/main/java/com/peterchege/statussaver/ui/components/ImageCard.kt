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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.R

@Composable
fun ImageCard(
    isSaved:Boolean,
    image: StatusFile,
    shareImage:(StatusFile) -> Unit,
    saveImage: (StatusFile) -> Unit,
    setActiveImage:(StatusFile) -> Unit,
) {
    val whatsappUri = if (image.isApi30) image.documentFile?.uri else image.file?.toUri()
    val savedUri = image.file
    val uri = if (isSaved) savedUri else whatsappUri

    Column(
        modifier = Modifier
            .height(170.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.LightGray)


    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(128.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(7.dp))
                .pointerInput(setActiveImage) {
                    detectTapGestures { setActiveImage(image) }
                }
                ,
            contentScale = ContentScale.FillBounds,
            model = uri,
            contentDescription = stringResource(id = R.string.whatsapp_image)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isSaved){
                CustomIconButton(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(id = R.string.download_image_description),
                    onClick = { saveImage(image) }
                )
            }
            CustomIconButton(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(id = R.string.share_image_description),
                onClick = { shareImage(image) }
            )

        }
    }
}