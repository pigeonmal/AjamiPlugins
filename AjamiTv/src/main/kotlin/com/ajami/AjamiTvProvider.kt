package com.ajami

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.ui.player.GeneratorPlayer
import com.lagradost.cloudstream3.ui.player.BasicLink
import com.lagradost.cloudstream3.ui.player.LinkGenerator
import com.lagradost.cloudstream3.CommonActivity.activity
import com.lagradost.cloudstream3.utils.UIHelper.navigate

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
    val channels = channelsMap[request.data]
    val livesList: List<LiveSearchResponse>? = channels?.map { channel ->
        LiveSearchResponse(
            channel.name,
            channel.toJson(),
            this.name,
            TvType.Live,
            posterUrl = channel.poster
        )
    }
    // Assuming newHomePageResponse accepts a list of SearchResponse
    val searchResponseList: List<SearchResponse> = livesList.orEmpty() // Convert to List<SearchResponse>
    return newHomePageResponse(request.name, searchResponseList)
}


    override suspend fun load(url: String): LoadResponse? {
        val channelData = parseJson<Channel>(url)
        val id = channelData.id.toString()
         activity?.navigate(
                R.id.global_to_navigation_player,
                GeneratorPlayer.newInstance(
                    LinkGenerator(
                        listOf(BasicLink("$mainUrl$id/index.m3u8")),
                        extract = true,
                        isM3u8 = true
                    )
                )
            )
    return null
//        return LiveStreamLoadResponse(
//            channelData.name,
//           "$mainUrl$id/index.m3u8",
//           this.name,
//           url,
//           channelData.poster
//     )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val channelData = parseJson<Channel>(data)
        val id = channelData.id.toString()

        callback.invoke(
            ExtractorLink(
                this.name,
                channelData.name,
              "$mainUrl$id/index.m3u8",
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
        val premiumBein1PosterUrl = "https://media.discordapp.net/attachments/1052649740732481569/1215345348818636820/beinpremium1.png?ex=65fc698f&is=65e9f48f&hm=78a133a0068ff47ab9176877bed0608aa013529367030a6656608de6480e293b&=&format=png&quality=lossless"
        val premiumBein2PosterUrl = "https://media.discordapp.net/attachments/1052649740732481569/1215346089889366016/beinpremium1.png?ex=65fc6a3f&is=65e9f53f&hm=6685ca814e33ecb5c48fd35bec8daf55485e748f89d3cdbabd3f156cf5e96f31&=&format=png&quality=lossless"
        val premiumBein3PosterUrl = "https://media.discordapp.net/attachments/1052649740732481569/1215346409835077765/beinpremium1.png?ex=65fc6a8c&is=65e9f58c&hm=3f8ba9140d55c59528cefa6f603415da735fe1caaa518c598283b009f6cee53f&=&format=png&quality=lossless"

        val BeinPremiumChannels = listOf(Channel("BEIN SPORTS 1 PREMIUM", 645242569, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS PREMIUM 1 HD LOCAL", 3290791558, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM 1080 LOCAL", 4171759985, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM 4K H265", 798204953, "Arabia", premiumBein1PosterUrl),Channel("BEINSPORT AR 1 PREMIUM", 3676879039, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS PREMIUM 1 FHD", 344625374, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM HEVC", 3509857289, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 1 PREMIUM (BACKUP)", 854273020, "Arabia", premiumBein1PosterUrl),Channel("BEIN SPORTS 2 PREMIUM 4K H265", 1418536698, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 2 FHD", 755477531, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM 1080 LOCAL", 2696325209, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM", 2613047815, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM HEVC", 1001582955, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 2 HD LOCAL", 3091487581, "Arabia", premiumBein2PosterUrl),Channel("BEINSPORT AR 2 PREMIUM", 1726141553, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS 2 PREMIUM (BACKUP)", 194217660, "Arabia", premiumBein2PosterUrl),Channel("BEIN SPORTS PREMIUM 3 FHD", 981252184, "Arabia", premiumBein3PosterUrl),Channel("BEINSPORT AR 3 PREMIUM", 3144945140, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM 1080 LOCAL", 563334526, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM", 1177103234, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS PREMIUM 3 HD LOCAL", 625719851, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM HEVC", 3571468170, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM 4K H265", 3411518820, "Arabia", premiumBein3PosterUrl),Channel("BEIN SPORTS 3 PREMIUM (BACKUP)", 482075260, "Arabia", premiumBein3PosterUrl))
        
        val channelsMap = mapOf("bp" to BeinPremiumChannels)
    }
}
