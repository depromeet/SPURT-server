package com.ssak3.timeattack.persona.service

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.PERSONA_NOT_FOUND_BY_ID
import com.ssak3.timeattack.persona.client.GoogleCloudProperties
import com.ssak3.timeattack.persona.client.YoutubeDataClient
import org.springframework.stereotype.Service

@Service
class PersonaService(
    private val youtubeDataClient: YoutubeDataClient,
    private val googleCloudProperties: GoogleCloudProperties,
) {

    fun getPlayListIds(personaId: Long): List<String> {
        val keyword = getKeyword(personaId.toInt()) ?: throw ApplicationException(PERSONA_NOT_FOUND_BY_ID, personaId)
        val response = youtubeDataClient.getVideos(
            key = googleCloudProperties.apiKey,
            searchKeyword = keyword
        )

        return response.items.map { it.id.videoId }
    }

    private fun getKeyword(personaId: Int): String? {
        val keywordMap = mapOf(
            1 to "시험 전날 벼락치기 플레이리스트",
            2 to "마감 글쓰기 플레이리스트",
            3 to "운동 전투력 상승하는 노래",
            4 to "집중력 폭발 EDM 코딩",
            5 to "디자인할때 케이팝 플레이리스트",
            6 to "과제 마감폭탄 노동요",
            7 to "신나는 공부 플레이리스트",
            8 to "글쓰기 신나는 팝송 플레이리스트 활기찬",
            9 to "고강도 운동 신나는 플레이리스트",
            10 to "코딩 노래 신나는 팝송",
            11 to "디자인 작업할때 듣는 노래 신나는",
            12 to "과제 플레이리스트 케이팝",
            13 to "공부 노래 카페 재즈",
            14 to "글쓰기 노래 서점 재즈",
            15 to "운동할 때 듣는 재즈",
            16 to "코딩 노래 카페 재즈 lofi",
            17 to "디자인 그림 노래 카페 재즈",
            18 to "과제 노래 카페 lofi",
            19 to "조용한 공부 노래 asmr 백색소음",
            20 to "도서관 asmr",
            21 to "요가 명상 asmr",
            22 to "코딩할때 듣는 자연의 소리",
            23 to "그림 asmr",
            24 to "화이트 노이즈 공부 음악",
        )

        return keywordMap[personaId]
    }
}
