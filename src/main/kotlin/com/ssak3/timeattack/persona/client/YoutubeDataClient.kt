package com.ssak3.timeattack.persona.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Component
@FeignClient(
    name = "youtubeDataClient",
    url = "https://www.googleapis.com/youtube/v3/search",
)
interface YoutubeDataClient {
    /**
     * 유튜브 비디오(플레이리스트) 조회
     */
    @GetMapping
    fun getVideos(
        @RequestParam("type") type: String = "video",
        @RequestParam("key") key: String,
        @RequestParam("q") searchKeyword: String,
    ): YoutubeSearchResponse
}
