package com.ajami

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import android.app.Activity

@CloudstreamPlugin
class AjamiTvPlugin: Plugin() {
    var activity: Activity? = null
    override fun load(context: Context) {
        activity = context as Activity
        // All providers should be added in this manner. Please don't edit the providers list directly.
        registerMainAPI(AjamiTvProvider(activity))
    }
}