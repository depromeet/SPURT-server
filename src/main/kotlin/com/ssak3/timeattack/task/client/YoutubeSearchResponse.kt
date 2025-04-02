package com.ssak3.timeattack.task.client

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
