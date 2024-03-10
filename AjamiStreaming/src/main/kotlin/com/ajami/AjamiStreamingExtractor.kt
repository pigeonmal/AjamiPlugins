package com.ajami

import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.Session
import java.net.URLDecoder
import java.util.Base64
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.apmap


val session = Session(Requests().baseClient)

object AjamiStreamingExtractor : AjamiStreamingProvider() {

    suspend fun invokeFrembed(
        id: Int? = null,
        season: Int? = null,
        episode: Int? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val url = if (season == null) {
            "$frembedAPI/film.php?id=$id"
        } else {
            "$frembedAPI/serie.php?id=$id&sa=$season&epi=$episode"
        }

        app.get(url).document.select("a.link-button").apmap { el ->
            val dataLink = el.attr("data-link")
            val decodedDataLink = String(Base64.getDecoder().decode(dataLink))
            val decodedURI = URLDecoder.decode(decodedDataLink, "UTF-8")
            loadExtractor(decodedURI, subtitleCallback, callback)
        }

        
    }


}