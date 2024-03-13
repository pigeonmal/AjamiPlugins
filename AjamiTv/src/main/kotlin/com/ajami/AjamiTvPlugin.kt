package com.ajami

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

@CloudstreamPlugin
class AjamiTvPlugin: Plugin() {
    override fun load(context: Context) {
        val activity = context as AppCompatActivity

        // All providers should be added in this manner. Please don't edit the providers list directly.
        registerMainAPI(AjamiTvProvider(activity))
    }
}