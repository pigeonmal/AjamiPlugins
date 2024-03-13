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
import com.lagradost.cloudstream3.R
import com.fasterxml.jackson.annotation.JsonProperty


class AjamiTvProvider : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://oha.to/play/"
    override var name = "AjamiTv"
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "ar"
    override val hasMainPage = true
    var channelsList: ArrayList<OhaChannel> = emptyList()

    data class wantedChannel(
        val group: String,
        val name: String,
        val poster: String,
    )
    companion object {
        val wantedChannels = listOf(
            wantedChannel("bp", "BEIN SPORTS PREMIUM 1 FHD |D", "https://media.discordapp.net/attachments/1052649740732481569/1215345348818636820/beinpremium1.png?ex=65fc698f&is=65e9f48f&hm=78a133a0068ff47ab9176877bed0608aa013529367030a6656608de6480e293b&=&format=png&quality=lossless"),
            wantedChannel("bp", "BEIN SPORTS PREMIUM 2 FHD |D", "https://media.discordapp.net/attachments/1052649740732481569/1215346089889366016/beinpremium1.png?ex=65fc6a3f&is=65e9f53f&hm=6685ca814e33ecb5c48fd35bec8daf55485e748f89d3cdbabd3f156cf5e96f31&=&format=png&quality=lossless"),
            wantedChannel("bp", "BEIN SPORTS PREMIUM 3 FHD |D", "https://media.discordapp.net/attachments/1052649740732481569/1215346409835077765/beinpremium1.png?ex=65fc6a8c&is=65e9f58c&hm=3f8ba9140d55c59528cefa6f603415da735fe1caaa518c598283b009f6cee53f&=&format=png&quality=lossless")
            ) 
    }


    override val mainPage = mainPageOf(
        "bp" to "⚽ Bein Sports Premium",
    )

  override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
    if (channelsList.isEmpty()) {
        val wantedChannelNames = wantedChannels.map { it.name }

        val listOhaChannels:ArrayList<OhaChannel> = app.get("https://oha.to/channels").parsedSafe<OhaChannels>()?.channels ?: emptyList()
             channelsList = listOhaChannels
                .filter { it.name in wantedChannelNames }
                .mapNotNull { ohaChannel ->
                    val wc = wantedChannels.find { it.name == ohaChannel.name }
                    wc?.let {
                      Channel(ohaChannel.name, ohaChannel.id, ohaChannel.country, it.poster, it.group)
                    }
                }.filterNotNull()
        
    }
    val channels = channelsList.filter { it.group == request.data }
    val livesList: List<LiveSearchResponse>? = channels.map { channel ->
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


    override suspend fun load(url: String): LoadResponse {
        val channelData = parseJson<Channel>(url)
        val idlive = channelData.id
         activity?.navigate(
                R.id.global_to_navigation_player,
                GeneratorPlayer.newInstance(
                    LinkGenerator(
                        listOf(BasicLink("$mainUrl$idlive/index.m3u8")),
                        extract = true,
                        isM3u8 = true
                    )
                )
            )
        return LiveStreamLoadResponse(
            channelData.name,
           "$mainUrl$idlive/index.m3u8",
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
        val id = channelData.id

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
        val id: String,
        val country: String,
        val poster: String,
        val group: String
    )

    data class OhaChannel(
        @JsonProperty("country") val country: String,
        @JsonProperty("id") val id: String,
        @JsonProperty("name") val name: String,
    )
    data class OhaChannels(
        @JsonProperty("channels") val results: ArrayList<OhaChannel>? = arrayListOf(),
    )



    override suspend fun search(query: String): List<SearchResponse> {
        return listOf<SearchResponse>()
    }
}
