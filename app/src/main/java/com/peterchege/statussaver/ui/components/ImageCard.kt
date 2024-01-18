package com.peterchege.statussaver.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import com.peterchege.statussaver.domain.models.StatusFile

@Composable
fun ImageCard(
    onSaveImage:() -> Unit,
    image:StatusFile,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp)
            .border(BorderStroke(2.dp, Color.Red))
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        val uri = if (image.isApi30) image.documentFile?.uri else image.file?.toUri()
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
            ,
            contentScale = ContentScale.FillWidth,
            model = uri,
            contentDescription = "Image"
        )
        Text(text = image.title ?:"(No name)")
    }

}