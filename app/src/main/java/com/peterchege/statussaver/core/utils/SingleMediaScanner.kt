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
package com.peterchege.statussaver.core.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import java.io.File


class SingleMediaScanner(context: Context?, private val mFile: File) : MediaScannerConnectionClient {
    private val mMs: MediaScannerConnection

    init {
        mMs = MediaScannerConnection(context, this)
        mMs.connect()
    }

    override fun onMediaScannerConnected() {
        mMs.scanFile(mFile.absolutePath, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mMs.disconnect()
    }
}