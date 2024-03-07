package com.ajami

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities

class AjamiTvProvider : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://oha.to/play/"
    override var name = "AjamiTv"
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "ar"
    override val hasMainPage = true

    override val mainPage = mainPageOf(
        "bp" to "⚽ Bein Sports Premium",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val channels = channelsMap[request.data];
        val livesList = channels?.map { channel ->
            LiveSearchResponse(
            channel.name,
            channel.toString(),
            this.name,
            TvType.Live,
            posterUrl = channel.poster,
        )
        }
        return newHomePageResponse(request.name, livesList)
    }

    override suspend fun load(url: String): LoadResponse {
        val channelData = parseJson<Channel>(url)
        return LiveStreamLoadResponse(
            channelData.name,
            mainUrl + channelData.id.toString() + "/index.m3u8",
            this.name,
            url,
            channelData.poster
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val channelData = parseJson<Channel>(data)

        callback.invoke(
            ExtractorLink(
                this.name,
                channelData.name,
                mainUrl + channelData.id.toString() + "/index.m3u8",
                "",
                Qualities.Unknown.value,
                isM3u8 = true
            )
        )
        return true
    }

    data class Channel(
        val name: String,
        val id: Long,
        val country: String,
        val poster: String
    )

    override suspend fun search(query: String): List<SearchResponse> {
        return listOf<SearchResponse>()
    }

    companion object {

        val premiumBein1PosterUrl = "https://i.goalzz.com/?i=o%2Fh%2F1%2F892%2Fbein-sports-premium-1.png"
        val premiumBein2PosterUrl = "https://i.goalzz.com/?i=o%2Fh%2F1%2F893%2Fbein-sports-premium-1.png"
        val premiumBein3PosterUrl = "https://i.goalzz.com/?i=o%2Fh%2F1%2F894%2Fbein-sports-premium-1.png"

        val BeinPremiumChannels = listOf(Channel("BEIN SPORTS 1 PREMIUM", 645242569, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS PREMIUM 1 HD LOCAL", 3290791558, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM 1080 LOCAL", 4171759985, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM 4K H265", 798204953, "Arabia", premiumBein1PosterUrl),Channel("BEINSPORT AR 1 PREMIUM", 3676879039, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS PREMIUM 1 FHD", 344625374, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM HEVC", 3509857289, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM (BACKUP)", 854273020, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 2 PREMIUM 4K H265", 1418536698, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 2 FHD", 755477531, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM 1080 LOCAL", 2696325209, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM", 2613047815, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM HEVC", 1001582955, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 2 HD LOCAL", 3091487581, "Arabia", premiumBein2PosterUrl),Channel("BEINSPORT AR 2 PREMIUM", 1726141553, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM (BACKUP)", 194217660, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 3 FHD", 981252184, "Arabia", premiumBein3PosterUrl),Channel("BEINSPORT AR 3 PREMIUM", 3144945140, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM 1080 LOCAL", 563334526, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM", 1177103234, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS PREMIUM 3 HD LOCAL", 625719851, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM HEVC", 3571468170, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM 4K H265", 3411518820, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM (BACKUP)", 482075260, "Arabia", premiumBein3PosterUrl))
        
        val channelsMap = mapOf("bp" to BeinPremiumChannels)
    }
}