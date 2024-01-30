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
package com.peterchege.statussaver.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.peterchege.statussaver.core.utils.Screens
import com.peterchege.statussaver.domain.models.BottomNavItem
import com.peterchege.statussaver.domain.models.StatusFile
import com.peterchege.statussaver.ui.screens.photos.AllPhotosScreen
import com.peterchege.statussaver.ui.screens.saved.SavedMediaScreen
import com.peterchege.statussaver.ui.screens.videos.AllVideosScreen
import com.peterchege.statussaver.R

@ExperimentalMaterial3Api
@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar {
        items.forEachIndexed { index, item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.name
                    )
                },
                label = { Text(text = item.name) },
                selected = selected,
                onClick = { onItemClick(item) }
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun BottomNavigation(
    shareImage: (StatusFile) -> Unit,
    shareVideo: (StatusFile) -> Unit,
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        name = stringResource(id = R.string.images),
                        route = Screens.ALL_WHATSAPP_IMAGES_SCREEN,
                        selectedIcon = Icons.Default.Image,
                        unselectedIcon = Icons.Outlined.Image,
                    ),

                    BottomNavItem(
                        name = stringResource(id = R.string.videos),
                        route = Screens.ALL_WHATSAPP_VIDEOS_SCREEN,
                        selectedIcon = Icons.Default.Videocam,
                        unselectedIcon = Icons.Outlined.Videocam
                    ),
                    BottomNavItem(
                        name = stringResource(id = R.string.saved),
                        route = Screens.ALL_SAVED_MEDIA_SCREEN,
                        selectedIcon = Icons.Default.Download,
                        unselectedIcon = Icons.Outlined.Download
                    )

                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screens.ALL_WHATSAPP_IMAGES_SCREEN
            ) {
                composable(
                    route = Screens.ALL_WHATSAPP_IMAGES_SCREEN,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() },
                ) {
                    AllPhotosScreen(
                        shareImage = shareImage,
                    )
                }
                composable(
                    route = Screens.ALL_WHATSAPP_VIDEOS_SCREEN,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() },
                ) {
                    AllVideosScreen(
                        shareVideo = shareVideo
                    )
                }

                composable(
                    route = Screens.ALL_SAVED_MEDIA_SCREEN,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() },
                ) {
                    SavedMediaScreen(
                        shareVideo = shareVideo,
                        shareImage = shareImage,
                    )

                }
            }
        }

    }
}