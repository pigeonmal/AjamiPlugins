package com.ajami

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class AjamiTvPlugin: Plugin() {
    override fun load(context: Context) {

        registerMainAPI(AjamiTvProvider())
    }
}