package com.peterchege.statussaver.domain.models

import androidx.documentfile.provider.DocumentFile
import java.io.File

data class StatusFile(
    val file: File?,
    val title:String?,
    val path:String?,
    val isVideo:Boolean ,
    val isApi30:Boolean,
    val documentFile: DocumentFile?
)