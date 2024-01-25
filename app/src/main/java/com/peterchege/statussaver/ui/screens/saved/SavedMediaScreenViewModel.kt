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
package com.peterchege.statussaver.ui.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.domain.repos.SavedStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SavedMediaScreenViewModel @Inject constructor(
    private val savedStatusRepository: SavedStatusRepository,
):ViewModel(){

    private val _statusFiles = MutableStateFlow<List<StatusFile>>(emptyList())
    val statusFiles = _statusFiles.asStateFlow()

    init {
        getStatusFiles()
    }

    fun getStatusFiles(){
        viewModelScope.launch {
            val savedStatusFiles = savedStatusRepository.getSavedStatus()
            _statusFiles.update { savedStatusFiles }

        }
    }

}