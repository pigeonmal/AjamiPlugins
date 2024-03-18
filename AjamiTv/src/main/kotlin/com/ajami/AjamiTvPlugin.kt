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

        registerMainAPI(AjamiTvProvider(this))
    }
}