package com.ajami

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.*


open class Frembed : ExtractorApi() {
    override val name = "Frembed"
    override val mainUrl = "https://frembed.fun"
    override val requiresReferer = false
    private val srcRegex2 = Regex("""player\.src\([\w\W]*src: "(.*?)"""")


    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url)
        val unpackedText = getAndUnpack(response.text)

        srcRegex2.find(unpackedText)?.groupValues?.get(1)?.let { link ->
            M3u8Helper.generateM3u8(
            name,
            link,
            url,
            Qualities.P1080.value,
        ).forEach(callback)
        }
    }

}