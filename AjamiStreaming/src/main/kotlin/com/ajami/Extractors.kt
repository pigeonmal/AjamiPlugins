package com.ajami

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.*


open class Frembed : ExtractorApi() {
    override val name = "Frembed"
    override val mainUrl = "https://frembed.fun"
    override val requiresReferer = false
    private val pattern = """src:\s*'([^']+)'""".toRegex()

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url)
        val unpackedText = getAndUnpack(response.text)
        val matches = pattern.find(unpackedText)

        // Extract the m3u8 link from the matches
        val m3u8Link = matches?.groups?.get(1)?.value
        M3u8Helper.generateM3u8(
            name,
            m3u8Link ?: return,
            url,
            Qualities.P1080.value,
        ).forEach(callback)
    }

}