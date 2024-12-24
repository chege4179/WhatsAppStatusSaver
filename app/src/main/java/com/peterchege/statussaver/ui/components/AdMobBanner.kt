package com.peterchege.statussaver.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.peterchege.statussaver.BuildConfig

@Composable
fun AdmobBanner(modifier: Modifier = Modifier) {
    val adId = if(BuildConfig.DEBUG) BuildConfig.ADMOB_BANNER_TEST_ID else BuildConfig.ADMOB_BANNER_PROD_ID
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // on below line specifying ad view.
            AdView(context).apply {
                // on below line specifying ad size
                //adSize = AdSize.BANNER
                // on below line specifying ad unit id
                // currently added a test ad unit id.
                setAdSize(AdSize.BANNER)
                adUnitId = adId
                // calling load ad to load our ad.
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}