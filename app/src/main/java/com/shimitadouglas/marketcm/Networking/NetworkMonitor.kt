package com.shimitadouglas.marketcm.Networking

import android.content.Context
import android.net.ConnectivityManager
import com.shimitadouglas.marketcm.R
import es.dmoral.toasty.Toasty

class NetworkMonitor(var context: Context) {
     fun checkInternet():Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInformation = connectivityManager.activeNetworkInfo
        return if (networkInformation != null) {
            true
        } else {
            Toasty.custom(
                context,
                "no internet connection!",
                R.drawable.ic_warning,
                R.color.androidx_core_secondary_text_default_material_light,
                Toasty.LENGTH_LONG,
                true,
                true
            ).show()
            false
        }
    }
}