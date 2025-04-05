package com.ssak3.timeattack.persona.service

import com.ssak3.timeattack.persona.client.GoogleCloudProperties
import com.ssak3.timeattack.persona.client.YoutubeDataClient
import org.springframework.stereotype.Service

@Service
class PersonaService(
    private val youtubeDataClient: YoutubeDataClient,
    private val googleCloudProperties: GoogleCloudProperties,
) {
    fun getPlaylists(id: Int): List<String>? {
        val playLists =
            mapOf(
                1 to
                    listOf(
                        "6NnV7jnhcEg",
                        "_63aV-Vz_cs",
                        "5WQTEu55YK4",
                        "hVfnCvcLn2Q",
                        "YKFY6BYAlfY",
                    ),
                2 to
                    listOf(
                        "TpPwI_Lo0YY",
                        "QUXKib-jfEM",
                        "afeArmye71g",
                        "ZP5_iSK6GVo",
                        "frYkZXCm8hI",
                    ),
                3 to
                    listOf(
                        "G6aDCGcjWDE",
                        "I2mo7a9XHnM",
                        "WnrIX9Ak1wA",
                        "-FoLIeLo3eQ",
                        "ZjXp9b1ZbqA",
                    ),
                4 to
                    listOf(
                        "t99W9uDdc_M",
                        "fcbIzugkrAY",
                        "qMwzWk81tVM",
                        "Xc1Le3CSdrM",
                        "3j4GgdJ8rTI",
                    ),
                5 to
                    listOf(
                        "7GO5lSPxOf0",
                        "9H-FgSB9Gt8",
                        "PE1_hAImGu4",
                        "aX1sLtxNF2Y",
                        "IbjzDob6ck0",
                    ),
                6 to
                    listOf(
                        "JKTJP92LFTk",
                        "qMwzWk81tVM",
                        "eQXibPTJemM",
                        "K65AV1GvUW0",
                        "r2ZHZz8unHI",
                    ),
                7 to
                    listOf(
                        "R1PMZuvX8zs",
                        "qMwzWk81tVM",
                        "ohsMB2Whyf4",
                        "ebPTIftheTw",
                        "7RCc04DUG9E",
                    ),
                8 to
                    listOf(
                        "x82tyBcB3WY",
                        "VA62qCryWYA",
                        "jim9K1vBL0k",
                        "ov0Mbm6FHu8",
                        "1sTc7I20u3A",
                    ),
                9 to
                    listOf(
                        "uJTaXCb38z0",
                        "FLfDwOL7HVs",
                        "e27XXlod5Q8",
                        "jim9K1vBL0k",
                        "VA62qCryWYA",
                    ),
                10 to
                    listOf(
                        "QyfBG_3kfPY",
                        "8Ji6HqcD2vE",
                        "jim9K1vBL0k",
                        "K65AV1GvUW0",
                        "1sTc7I20u3A",
                    ),
                11 to
                    listOf(
                        "7GO5lSPxOf0",
                        "9H-FgSB9Gt8",
                        "PE1_hAImGu4",
                        "aX1sLtxNF2Y",
                        "IbjzDob6ck0",
                    ),
                12 to
                    listOf(
                        "hVfnCvcLn2Q",
                        "qMwzWk81tVM",
                        "eQXibPTJemM",
                        "K65AV1GvUW0",
                        "r2ZHZz8unHI",
                    ),
                13 to
                    listOf(
                        "hiMoy4pyAl0",
                        "ir4g7-DZG9w",
                        "CrfsnUklH4k",
                        "AnqkPWH3A6I",
                        "pIfMx45aEOU",
                    ),
                14 to
                    listOf(
                        "c_l1ZwJbAnc",
                        "gnOiM2NOgGI",
                        "AA3UPI7WBkc",
                        "LpmCRZK-0c8",
                        "PA7Um65aBHI",
                    ),
                15 to
                    listOf(
                        "AA3UPI7WBkc",
                        "eQXibPTJemM",
                        "c_l1ZwJbAnc",
                        "VA62qCryWYA",
                        "gnOiM2NOgGI",
                    ),
                16 to
                    listOf(
                        "vC5wd4QVa48",
                        "D9IVOL9felk",
                        "gnOiM2NOgGI",
                        "eQXibPTJemM",
                        "c_l1ZwJbAnc",
                    ),
                17 to
                    listOf(
                        "vIiOyIc4MDY",
                        "akWcPHXcEjQ",
                        "YK3ot-oM1AU",
                        "eQXibPTJemM",
                        "yAlkj7y7kD4",
                    ),
                18 to
                    listOf(
                        "jim9K1vBL0k",
                        "qMwzWk81tVM",
                        "hYTpu9fMn1w",
                        "TacJb3LerGQ",
                        "CGkUF62HABQ",
                    ),
                19 to
                    listOf(
                        "2TrgSww4Wf8",
                        "5ss2qOtJf4U",
                        "b78FLPvTOvk",
                        "wIBnaNuhuCQ",
                        "nRu9brcdEGo",
                    ),
                20 to
                    listOf(
                        "wIBnaNuhuCQ",
                        "757G_El3ABI",
                        "BGnRjGdGnCE",
                        "InZ_XAs0-nM",
                        "m0KCzeeyxcY",
                    ),
                21 to
                    listOf(
                        "2N4eTTipm9I",
                        "TsZ6K6RLb-4",
                        "mWASFFB8YFY",
                        "B9GsLAPeA2M",
                        "-bGZS8wr-mU",
                    ),
                22 to
                    listOf(
                        "Yuw8TnTei58",
                        "5fQgDJrGsuE",
                        "lYJ17n_oUkA",
                        "MUVpFPRImgA",
                        "m876TGnxFxQ",
                    ),
                23 to
                    listOf(
                        "yncnafsDK4k",
                        "m_2DoZt932I",
                        "nVuHD8p_Wqc",
                        "JOzLYxRSQWo",
                        "eQXibPTJemM",
                    ),
                24 to
                    listOf(
                        "ZTgzeACTogw",
                        "xRuHxlUoy2s",
                        "v88tm1G78yg",
                        "XnZtxrFsaw4",
                        "GH69Np4PVAU",
                    ),
            )

        return playLists[id]
    }

    //    fun getPlayListIds(personaId: Long): List<String> {
//        val keyword = getKeyword(personaId.toInt()) ?: throw ApplicationException(PERSONA_NOT_FOUND_BY_ID, personaId)
//        val response =
//            youtubeDataClient.getVideos(
//                key = googleCloudProperties.apiKey,
//                searchKeyword = keyword,
//            )
//
//        return response.items.map { it.id.videoId }
//    }

    private fun getKeyword(personaId: Int): String? {
        val keywordMap =
            mapOf(
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
