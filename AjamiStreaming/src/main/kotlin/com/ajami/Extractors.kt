package com.ajami

import com.lagradost.cloudstream3.utils.*

open class Frembed : ExtractorApi() {
    override val name = "Frembed"
    override val mainUrl = "https://frembed.fun"

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url)
        val script = if (!getPacked(response.text).isNullOrEmpty()) {
            getAndUnpack(response.text)
        } else {
            response.document.selectFirst("script:containsData(sources:)")?.data()
        }
        val m3u8 =
            Regex("src:\\s*\"(.*?m3u8.*?)\"").find(script ?: return)?.groupValues?.getOrNull(1)
        M3u8Helper.generateM3u8(
            name,
            m3u8 ?: return,
            url
        ).forEach(callback)
    }

}