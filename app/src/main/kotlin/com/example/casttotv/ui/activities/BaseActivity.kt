package com.example.casttotv.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.casttotv.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

open class BaseActivity : AppCompatActivity() {
    var mInterstitialAd: InterstitialAd? = null
    private var nativeAd: NativeAd? = null

    //    var trueFalse: TrueFalse? = null
//    var adClosed: AdClosed? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the Mobile Ads SDK.
//        MobileAds.initialize(this) { createPersonalizedInterstitial() }
    }


    private fun createPersonalizedInterstitial() {
        val adRequest = AdRequest.Builder().build()
//        initInterstitial(adRequest)
    }

//    fun showInterstitialActivity(adClosed: AdClosed) {
//        this.adClosed = adClosed
//        if (mInterstitialAd != null) {
//            Log.d("AdMob", "On ad show")
//            mInterstitialAd!!.show(this)
//        } else {
//            Log.d("AdMob", "The interstitial ad wasn't ready yet.")
//            adClosed.addFailed(true)
//        }
//    }
//
//    fun showInterstitialActivity(adClosed: AdClosed, play: Boolean) {
//        this.adClosed = adClosed
//        if (play) {
//            if (mInterstitialAd != null) {
//                Log.d("AdMob", "On ad show")
//                mInterstitialAd!!.show(this)
//            } else {
//                Log.d("AdMob", "The interstitial ad wasn't ready yet.")
//                adClosed.addFailed(true)
//            }
//        } else {
//            adClosed.addFailed(true)
//        }
//    }

//    fun initInterstitial(adRequest: AdRequest?) {
//        InterstitialAd.load(this, getString(R.string.interstitial),
//            adRequest, object : InterstitialAdLoadCallback() {
//                fun onAdLoaded(@NonNull interstitialAd: InterstitialAd?) {
//                    // The mInterstitialAd reference will be null until
//                    // an ad is loaded.
//                    mInterstitialAd = interstitialAd
//                    mInterstitialAd!!.setFullScreenContentCallback(object :
//                        FullScreenContentCallback() {
//                        fun onAdDismissedFullScreenContent() {
//                            adClosed.addDismissed(true)
//                            createPersonalizedInterstitial()
//                        }
//
//                        fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                            Log.d("AdMob", "The ad failed to show.")
//                            adClosed.addFailed(true)
//                        }
//
//                        fun onAdShowedFullScreenContent() {
//                            // Called when fullscreen content is shown.
//                            // Make sure to set your reference to null so you don't
//                            // show it a second time.
//                            mInterstitialAd = null
//                            Log.d("AdMob", "The ad was shown.")
//                        }
//                    })
//                }
//
//                fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError?) {
//                    mInterstitialAd = null
//                    adClosed.addFailed(true)
//                }
//            })
//    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)


        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc: VideoController? = nativeAd.mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null && vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    //                     videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd()
                }
            }
        }
    }

    private fun populateBrowser(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)


        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc: VideoController? = nativeAd.mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null && vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    //                     videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd()
                }
            }
        }
    }

    private fun populateNativeAdView2(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media) as MediaView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        //        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let {
            adView.mediaView?.setMediaContent(it)
        }
        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc: VideoController? = nativeAd.mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null && vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    //                     videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd()
                }
            }
        }
    }

    private fun populateNativeAdViewSmall(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
//        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline))
        adView.setBodyView(adView.findViewById(R.id.ad_body))
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action))
        adView.setIconView(adView.findViewById(R.id.ad_app_icon))

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
        val vc: VideoController? = nativeAd.mediaContent?.videoController
        vc?.let {
            if (it.hasVideoContent()) {
                it.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
            }
        }

    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    fun refreshAd(frameLayout: CardView) {
        val builder = AdLoader.Builder(this, getString(R.string.native_advanced))

        // OnLoadedListener implementation.
        builder.forNativeAd { nativeAd ->
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            //                        refresh.setEnabled(true);
            val isDestroyed: Boolean = isDestroyed
            if (isDestroyed || this@BaseActivity.isFinishing || this@BaseActivity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (this@BaseActivity.nativeAd != null) {
                this@BaseActivity.nativeAd?.destroy()
            }
            this@BaseActivity.nativeAd = nativeAd
            val adView: NativeAdView =
                this@BaseActivity.layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                refresh.setEnabled(true);
                        val error = String.format("domain: %s, code: %d, message: %s",
                            loadAdError.getDomain(),
                            loadAdError.getCode(),
                            loadAdError.getMessage())
                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun refreshAdBrowser(frameLayout: FrameLayout) {
        val builder = AdLoader.Builder(this, getString(R.string.native_advanced))

        // OnLoadedListener implementation.
        builder.forNativeAd { nativeAd ->
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            //                        refresh.setEnabled(true);
            val isDestroyed: Boolean = isDestroyed
            if (isDestroyed || this@BaseActivity.isFinishing || this@BaseActivity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (this@BaseActivity.nativeAd != null) {
                this@BaseActivity.nativeAd?.destroy()
            }
            this@BaseActivity.nativeAd = nativeAd
            val adView: NativeAdView =
                this@BaseActivity.layoutInflater.inflate(R.layout.ad_browser, null) as NativeAdView
            populateBrowser(nativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                refresh.setEnabled(true);
                        val error = String.format("domain: %s, code: %d, message: %s",
                            loadAdError.domain,
                            loadAdError.getCode(),
                            loadAdError.getMessage())
                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun refreshAd2(frameLayout: FrameLayout) {
//        refresh.setEnabled(false);
        val builder = AdLoader.Builder(this, getString(R.string.native_advanced))

        // OnLoadedListener implementation.
        builder.forNativeAd { nativeAd ->
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            //                        refresh.setEnabled(true);
            val isDestroyed: Boolean = isDestroyed
            if (isDestroyed || this@BaseActivity.isFinishing || this@BaseActivity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (this@BaseActivity.nativeAd != null) {
                this@BaseActivity.nativeAd?.destroy()
            }
            this@BaseActivity.nativeAd = nativeAd
            val adView: NativeAdView =
                this@BaseActivity.layoutInflater.inflate(R.layout.ad_unified_exit,
                    null) as NativeAdView
            populateNativeAdView2(nativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder
            .withAdListener(
                object : AdListener() {
                    @SuppressLint("DefaultLocale")
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                refresh.setEnabled(true);
//                        val error = String.format("domain: %s, code: %d, message: %s",
//                        loadAdError.domain,
//                        loadAdError.code,
//                        loadAdError.message)
                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun refreshAdSmallNative(frameLayout: FrameLayout) {
        val builder = AdLoader.Builder(this, getString(R.string.native_advanced))

        builder.forNativeAd { nativeAd ->
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            val isDestroyed: Boolean = isDestroyed
            if (isDestroyed || this@BaseActivity.isFinishing || this@BaseActivity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (this@BaseActivity.nativeAd != null) {
                this@BaseActivity.nativeAd?.destroy()
            }
            this@BaseActivity.nativeAd = nativeAd
            val adView: NativeAdView =
                this@BaseActivity.layoutInflater.inflate(R.layout.ad_unified_small,
                    null) as NativeAdView
            populateNativeAdViewSmall(nativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                refresh.setEnabled(true);
//                        val error = String.format("domain: %s, code: %d, message: %s",
//                            loadAdError.domain,
//                            loadAdError.getCode(),
//                            loadAdError.getMessage())
                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd?.destroy()
        }
        super.onDestroy()
    }
}