package com.peterchege.statussaver.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import com.peterchege.statussaver.core.utils.createVideoThumb
import com.peterchege.statussaver.domain.models.StatusFile

@Composable
fun VideoCard(
    onSaveVideo: () -> Unit,
    video: StatusFile,
) {
    val context = LocalContext.current
    val uri = if (video.isApi30) video.documentFile?.uri else video.file?.toUri()
    val imageBitmap = createVideoThumb(context = context,uri = uri)

    SubcomposeAsyncImage(
        modifier = Modifier
            .height(128.dp)
            .width(100.dp)
            .pointerInput(onSaveVideo) {
                detectTapGestures { onSaveVideo() }
            }
            .border(BorderStroke(2.dp, Color.Red))
        ,
        contentScale = ContentScale.FillWidth,
        model = imageBitmap,
        contentDescription = "Image"
    )
}