package com.ajami

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.ui.player.GeneratorPlayer
import com.lagradost.cloudstream3.ui.player.BasicLink
import com.lagradost.cloudstream3.ui.player.LinkGenerator
import com.lagradost.cloudstream3.R
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.core.type.TypeReference

class AjamiTvProvider() : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://oha.to/play/"
    override var name = "AjamiTv"
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "ar"
    override val hasMainPage = true
    var channelsList: List<OhaChannel> = emptyList()

    data class wantedChannel(
        val group: String,
        val name: String,
        val poster: String,
        val unwantedChannels: List<String>? = null
    )
    companion object {
        val wantedChannels = listOf(
            wantedChannel("bp", "BEIN SPORTS PREMIUM 1", "https://media.discordapp.net/attachments/1052649740732481569/1215345348818636820/beinpremium1.png?ex=65fc698f&is=65e9f48f&hm=78a133a0068ff47ab9176877bed0608aa013529367030a6656608de6480e293b&=&format=png&quality=lossless"),
            wantedChannel("bp", "BEIN SPORTS PREMIUM 2", "https://media.discordapp.net/attachments/1052649740732481569/1215346089889366016/beinpremium1.png?ex=65fc6a3f&is=65e9f53f&hm=6685ca814e33ecb5c48fd35bec8daf55485e748f89d3cdbabd3f156cf5e96f31&=&format=png&quality=lossless"),
            wantedChannel("bp", "BEIN SPORTS PREMIUM 3", "https://media.discordapp.net/attachments/1052649740732481569/1215346409835077765/beinpremium1.png?ex=65fc6a8c&is=65e9f58c&hm=3f8ba9140d55c59528cefa6f603415da735fe1caaa518c598283b009f6cee53f&=&format=png&quality=lossless"),
            wantedChannel("bp", "BEIN SPORTS 1", "https://logowik.com/content/uploads/images/bein-sports-13097.logowik.com.webp", listOf("premium", "max")),
            wantedChannel("bp", "BEIN SPORTS 2", "https://logowik.com/content/uploads/images/bein-sports-25584.logowik.com.webp", listOf("premium", "max")),
            wantedChannel("bp", "BEIN SPORTS 3", "https://logowik.com/content/uploads/images/bein-sports-31536.logowik.com.webp", listOf("premium", "max")),
            wantedChannel("bp", "BEIN SPORTS 4", "https://www.infosfoot.net/wp-content/uploads/2021/04/a3b61862a7fee714bf609af50e419b57.jpg", listOf("premium", "max")),
            wantedChannel("bp", "BEIN SPORTS 5", "https://static.wikia.nocookie.net/logopedia/images/8/87/BS5.svg/revision/latest?cb=20200107204133", listOf("premium", "max")),
            )
        val forbiddenWords = listOf("sd", "1mb", "low", "(backup)")
    }


    override val mainPage = mainPageOf(
        "bp" to "⚽ Bein Sports",
    )

  override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
    if (page >= 2) {
        return null;
    }
    if (channelsList.isEmpty()) {
        val wantedChannelNames = wantedChannels.map { it.name }

        val req = app.get("https://oha.to/channels")
        val mapper = jacksonObjectMapper()

        val typeReference = object : TypeReference<List<OhaChannel>>() {}

        val listOhaChannels: List<OhaChannel> = mapper.readValue(req.text, typeReference)
        channelsList = listOhaChannels
       /* val filtredChannels:List<Channel> = listOhaChannels
        .filter { it.name in wantedChannelNames }
        .mapNotNull { ohaChannel ->
            val wc = wantedChannels.find { it.name == ohaChannel.name }
            wc?.let {
                Channel(ohaChannel.name, ohaChannel.id.toString(), ohaChannel.country, wc.poster, wc.group)
            }
        }
        .filterNotNull()
        channelsList = filtredChannels*/
        
    }
    val channels = wantedChannels.filter { it.group == request.data }
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
        val channelData = parseJson<wantedChannel>(url)
 
        return LiveStreamLoadResponse(
            channelData.name,
           channelData.name,
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
        val channelData = parseJson<wantedChannel>(data)
        val searchname = channelData.name
        val splitedWords = searchname.toLowerCase().split(" ")
        channelsList.filter { channel ->
            val channelName = channel.name
            val splitedChannelName = channelName.toLowerCase().split(" ")
            splitedWords.all { item -> splitedChannelName.contains(item) } && (forbiddenWords + (channel.unwantedChannels ?: emptyList())).none { item -> splitedChannelName.contains(item) }
        }.forEach { channel ->
            callback.invoke(
                ExtractorLink(
                    this.name,
                    channel.name,
                    "$mainUrl${channel.id.toString()}/index.m3u8",
                    "https://oha.to/",
                    Qualities.Unknown.value,
                    isM3u8 = true
                    )
            )
        }
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
        @JsonProperty("id") val id: Long,
        @JsonProperty("name") val name: String,
        @JsonProperty("p") val p: Int?=null
    )

    override suspend fun search(query: String): List<SearchResponse> {
        return listOf<SearchResponse>()
    }
}
