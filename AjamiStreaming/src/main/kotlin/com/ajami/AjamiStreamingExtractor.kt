package com.ajami

import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.Session
import java.net.URLDecoder
import android.util.Base64
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.SubtitleHelper
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.apmap


val session = Session(Requests().baseClient)

data class OsSubtitles(
        @JsonProperty("url") val url: String? = null,
        @JsonProperty("lang") val lang: String? = null,
        @JsonProperty("g") val stringOffset: String? = null,
    )

data class OsResult(
    @JsonProperty("subtitles") val subtitles: ArrayList<OsSubtitles>? = arrayListOf(),
)

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
            val decodedDataLink = String(Base64.decode(dataLink, Base64.DEFAULT))
            val decodedURI = URLDecoder.decode(decodedDataLink, "UTF-8")
            loadExtractor(decodedURI, subtitleCallback, callback)
        }
    }

    suspend fun invokeOpenSubs(
        imdbId: String? = null,
        season: Int? = null,
        episode: Int? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
    ) {
        val slug = if(season == null) {
            "movie/$imdbId"
        } else {
            "series/$imdbId:$season:$episode"
        }
        app.get("${openSubAPI}/subtitles/$slug.json").parsedSafe<OsResult>()?.subtitles?.map { sub ->
            val offset: Long? = sub.stringOffset?.toLongOrNull()

                subtitleCallback.invoke(
                    SubtitleFile(
                        SubtitleHelper.fromThreeLettersToLanguage(sub.lang ?: "") ?: sub.lang
                        ?: return@map,
                        sub.url ?: return@map,
                        offset
                    )
                )
            
        }
    }


}