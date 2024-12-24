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
package com.peterchege.statussaver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.peterchege.statussaver.core.di.IoDispatcher
import com.peterchege.statussaver.ui.navigation.BottomNavigation
import com.peterchege.statussaver.ui.theme.WhatsAppStatusSaverTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject




@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val TAG = MainActivity::class.java.simpleName

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private var AD_UNIT_ID =
        if (BuildConfig.DEBUG) BuildConfig.ADMOB_INTERSTITIAL_TEST_ID else BuildConfig.ADMOB_INTERSTITIAL_PROD_ID


    private val REQUEST_PERMISSIONS = 1234

    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val NOTIFICATION_PERMISSION = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val NOTIFICATION_REQUEST_PERMISSIONS = 4


    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    contentResolver.takePersistableUriPermission(
                        it.data!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (arePermissionDenied()) {
            // If Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionQ()

            }
            requestPermissions(PERMISSIONS,REQUEST_PERMISSIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                NOTIFICATION_PERMISSION,
                NOTIFICATION_REQUEST_PERMISSIONS
            )
        }
        val backgroundScope = CoroutineScope(ioDispatcher)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }


        var adRequest = AdRequest.Builder().build()


        setContent {
            val context = LocalContext.current

            var interStitialAd by remember {
                mutableStateOf<InterstitialAd?>(null)
            }

            LaunchedEffect(key1 = Unit) {
                InterstitialAd.load(context, AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Timber.tag(TAG).d("Failed to load ADs ${adError.message}")
                        interStitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Timber.tag(TAG).d("AD loaded")
                        interStitialAd = interstitialAd


                    }
                })


                interStitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Timber.tag(TAG).d("Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Timber.tag(TAG).d("Ad dismissed fullscreen content.")
                        interStitialAd = null
                    }


                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Timber.tag(TAG).d("Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Timber.tag(TAG).d("Ad showed fullscreen content.")
                    }
                }
            }


            WhatsAppStatusSaverTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    BottomNavigation(
                        interstitialAd = interStitialAd,
                        shareImage = {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.setType("image/jpg")
                            if (it.isApi30) {
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    it.documentFile?.uri
                                )
                            } else {
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    Uri.parse("file://" + it.file?.absolutePath)
                                )
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share image"))
                        },
                        shareVideo = {
                            val shareIntent = Intent(Intent.ACTION_SEND)

                            shareIntent.setType("image/mp4")
                            if (it.isApi30) {
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    it.documentFile?.uri
                                )
                            } else {
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    Uri.parse("file://" + it.file?.absolutePath)
                                )
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share Video"))
                        }
                    )
                }
            }
        }
    }

    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return contentResolver.persistedUriPermissions.size <= 0
        }
        for (permissions in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    permissions
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun requestPermissionQ() {
        val sm = getSystemService(STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
        Timber.tag(TAG).d(uri.toString())
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.setFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        activityResultLauncher.launch(intent)
    }
}
