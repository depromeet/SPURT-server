package com.ssak3.timeattack.persona.client

data class YoutubeSearchResponse(
    val items: List<Item>,
) {
    data class Item(
        val id: Video,
    ) {
        data class Video(
            val kind: String,
            val videoId: String,
        )
    }
}
