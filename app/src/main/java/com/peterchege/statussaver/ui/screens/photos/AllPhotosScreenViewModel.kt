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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.models.WhatsAppPhoto
import com.peterchege.statussaver.domain.repos.WhatsAppImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AllPhotosScreenViewModel @Inject constructor(
    private val imagesRepository: WhatsAppImagesRepository,
): ViewModel() {
    private val TAG = AllPhotosScreenViewModel::class.java.simpleName

    private val _photos = MutableStateFlow<List<StatusFile>>(emptyList())
    val photos = _photos.asStateFlow()


    init {
        viewModelScope.launch {
            val newPhotos = imagesRepository.getAllWhatsAppStatusImages()
            Timber.tag(TAG).i("Photos >>>>..${newPhotos}")
            _photos.update {
                if (newPhotos != null){
                    it + newPhotos
                }else{
                    it
                }
            }
        }
    }

}