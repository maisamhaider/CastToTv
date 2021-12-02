package com.example.casttotv.openvpn

import android.net.Uri
import com.example.casttotv.R

object Utils {
    /**
     * Convert drawable image resource to string
     *
     * @param resourceId drawable image resource
     * @return image path
     */
    @JvmStatic
    fun getImgURL(resourceId: Int): String {

        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resourceId)
            .toString()
    }
}