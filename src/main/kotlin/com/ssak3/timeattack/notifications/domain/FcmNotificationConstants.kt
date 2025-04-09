package com.ssak3.timeattack.notifications.domain

object FcmNotificationConstants {
    fun getMessage(index: Int): String {
        val messages =
            taskBeforeMessageTemplate[index] ?: throw IllegalStateException("message not exist for this number")
        return messages
    }

    fun getRemindMessage(index: Int): String {
        val message = remindMessageTemplate[index] ?: throw IllegalStateException("message not exist for this number")
        return message
    }

    fun getSupportMessage(
        personaId: Int,
        personaName: String,
        nickname: String,
        index: Int,
    ): String {
        val supportMessage =
            supportMessageTemplate[personaId] ?: throw IllegalStateException("message not exist for this number")
        val message =
            """
            $personaName ${nickname}님
            ${supportMessage[index]}
            """.trimIndent()
        return message
    }

    fun getRoute(order: Int): String {
        // 0보다 작을 경우 = 응원 문구 푸시 알림
        return if (order < 0) {
            "/"
        } else if (order == 0) {
            "/action/start"
        } else {
            "/action/push?left=${REMINDER_LIMIT - order}"
        }
    }

    private const val REMINDER_LIMIT = 3

    private val taskBeforeMessageTemplate =
        mapOf(
            0 to
                """
                작업 시간이 다 되었어요!
                작은 행동부터 시작해볼까요?
                """.trimIndent(),
            1 to
                """
                아직 할 일을 시작하지 않으셨네요.
                그리 어렵지 않아요. 1분만 투자해볼까요?
                """.trimIndent(),
            2 to
                """
                혹시 까먹으셨나요? 아직 시작하지 않으셨어요!
                시작 타이밍, 지금 딱 좋아요. 기회는 계속 안 옵니다!
                """.trimIndent(),
            3 to
                """
                이제 3번 중 2번 남았습니다. 더 미루면 놓쳐요!
                다음 알림까진 2분… 진짜 시작해볼까요?
                """.trimIndent(),
            4 to
                """
                마지막 기회 하나 남았습니다. 진짜 이번엔 시작해야 해요.
                이제 끝입니다. 알림은 여기까지만 참을게요!
                """.trimIndent(),
            5 to
                """
                더 이상 알림은 없습니다. 지금이 진짜 마지막 찬스 🔥
                지금 안 하면 오늘도 미룸 예약!
                """.trimIndent(),
        )

    private val remindMessageTemplate =
        mapOf(
            0 to
                """
                이제 두 번의 기회만 남았어요!
                미루기 전에 얼른 시작해볼까요?
                """.trimIndent(),
            1 to
                """
                한번만 더 알림오고 끝이에요!
                작업을 미루기 전에 얼른 시작해보세요!
                """.trimIndent(),
            2 to
                """
                이게 마지막 기회에요!
                더 미루면 알림도 포기할거에요. 당장 시작하세요!
                """.trimIndent(),
        )

    /**
     * index
     * 1 > 매일 아침 9시
     * 2 > 중간지점
     * 3 > 마감 24시간 전
     * 4 > 마감 1시간 전
     * 5 > 마감 10분 전
     */
    private val supportMessageTemplate =
        mapOf(
            1 to
                listOf(
                    "",
                    """
                    기관차는 멈추지 않아요! 오늘도 전력 질주!🚂
                    """.trimIndent(),
                    """
                    절반 왔어요! 남은 시간, 더 빨리 달려볼까요?⚡️
                    """.trimIndent(),
                    """
                    마감 하루 전! 남은 시간, 더 빨리 달려볼까요?⚡️
                    """.trimIndent(),
                    """
                    마지막 1시간! 막판 스퍼트 올려서 전력 질주!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 막판 스퍼트 올려서 전력 질주!🔥
                    """.trimIndent(),
                ),
            2 to
                listOf(
                    "",
                    """
                    오늘도 한 줄 한 줄, 마감까지 불태워봐요!🔥
                    """.trimIndent(),
                    """
                    절반 왔어요! 마감까지 계속 써볼까요?🚀
                    """.trimIndent(),
                    """
                    마감 하루 전! 마감까지 계속 써볼까요?🚀
                    """.trimIndent(),
                    """
                    마지막 1시간! 손가락에 불나게 스퍼트 ON!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 손가락에 불나게 스퍼트 ON!💥
                    """.trimIndent(),
                ),
            3 to
                listOf(
                    "",
                    """
                    오늘도 심박수 폭주 예약! 한 세트라도 해봐요!️‍❤️‍🔥
                    """.trimIndent(),
                    """
                    절반 왔어요! 남은 세트도 끝까지 가요!💪
                    """.trimIndent(),
                    """
                    마감 하루 전! 남은 세트도 끝까지 가요!💪
                    """.trimIndent(),
                    """
                    마지막 1시간! 막판 스퍼트로 심박수 올려요!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 막판 스퍼트로 심박수 올려요!🔥
                    """.trimIndent(),
                ),
            4 to
                listOf(
                    "",
                    """
                    신호는 초록불! 오늘도 첫 커밋부터 스피드업!🚥
                    """.trimIndent(),
                    """
                    절반 왔어요! 디버깅 없이 풀가속 유지!🚀
                    """.trimIndent(),
                    """
                    마감 하루 전! 디버깅 없이 풀가속 유지!🚀
                    """.trimIndent(),
                    """
                    마지막 1시간! 배포까지 막판 스퍼트 ON!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 배포까지 막판 스퍼트 ON!💥
                    """.trimIndent(),
                ),
            5 to
                listOf(
                    "",
                    """
                    마감 요정 출근 완료! 손만 대도 완성도 UP!🎨
                    """.trimIndent(),
                    """
                    절반 왔어요! 마감까지 스피드 올려봐요!🖌
                    """.trimIndent(),
                    """
                    마감 하루 전! 마감까지 스피드 올려봐요!🖌
                    """.trimIndent(),
                    """
                    마지막 1시간! 마지막 터치까지 스퍼트 ON!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 마지막 터치까지 스퍼트 ON!🔥
                    """.trimIndent(),
                ),
            6 to
                listOf(
                    "",
                    """
                    과제는 준비 됐어요. 오늘도 조금만 해볼까요?✍️
                    """.trimIndent(),
                    """
                    절반 왔어요! 남은 시간도 같이 달려봐요!⏳
                    """.trimIndent(),
                    """
                    마감 하루 전! 남은 시간도 같이 달려봐요!⏳
                    """.trimIndent(),
                    """
                    마지막 1시간! 스퍼트 올려서 눈물 닦고 끝까지!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 스퍼트 올려서 눈물 닦고 끝까지!🔥
                    """.trimIndent(),
                ),
            7 to
                listOf(
                    "",
                    """
                    부릉! 브레인 워밍업하러 가볼까요?🏎💨
                    """.trimIndent(),
                    """
                    절반 왔어요! 남은 시간도 이 기세로 신나게!🎸
                    """.trimIndent(),
                    """
                    마감 하루 전! 남은 시간도 이 기세로 신나게!🎸
                    """.trimIndent(),
                    """
                    마지막 1시간! 하이텐션으로 스퍼트 ON!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 하이텐션으로 스퍼트 ON!🔥
                    """.trimIndent(),
                ),
            8 to
                listOf(
                    "",
                    """
                    글감도 리듬도 준비 완료! 한 줄씩 채워봐요!✍️
                    """.trimIndent(),
                    """
                    절반 왔어요! 이대로 남은 글도 계속 써요!🎯
                    """.trimIndent(),
                    """
                    마감 하루 전! 이대로 남은 글도 계속 써요!🎯
                    """.trimIndent(),
                    """
                    마지막 1시간! 스퍼트 ON! 비트도 최대로!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 스퍼트 ON! 비트도 최대로!💥
                    """.trimIndent(),
                ),
            9 to
                listOf(
                    "",
                    """
                    오늘도 신나게! 한 세트만 워밍업 시작해봐요!💪
                    """.trimIndent(),
                    """
                    절반 왔어요! 펌핑하기 딱 좋은 타이밍!🏋
                    """.trimIndent(),
                    """
                    마감 하루 전! 펌핑하기 딱 좋은 타이밍!🏋
                    """.trimIndent(),
                    """
                    마지막 1시간! 최고 컨디션으로 끝까지 스퍼트!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 최고 컨디션으로 끝까지 스퍼트!💥
                    """.trimIndent(),
                ),
            10 to
                listOf(
                    "",
                    """
                    오늘도 완벽한 코드 믹싱, 첫 커밋 스타트!🏁
                    """.trimIndent(),
                    """
                    절반 왔어요! 이 흐름대로, 완벽한 코드 완성!🚀
                    """.trimIndent(),
                    """
                    마감 하루 전! 이 흐름대로, 완벽한 코드 완성!🚀
                    """.trimIndent(),
                    """
                    마지막 1시간! 키보드 풀파워로 스퍼트 올려요!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 키보드 풀파워로 스퍼트 올려요!🔥
                    """.trimIndent(),
                ),
            11 to
                listOf(
                    "",
                    """
                    손끝의 리듬감을 살려, 오늘도 한 컷씩 GO!🎶
                    """.trimIndent(),
                    """
                    절반 왔어요! 단축키 리듬 놓치지 마요!🥁
                    """.trimIndent(),
                    """
                    마감 하루 전! 단축키 리듬 놓치지 마요!🥁
                    """.trimIndent(),
                    """
                    마지막 1시간! 스퍼트 올려서 단축키 폭풍 클릭!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 스퍼트 올려서 단축키 폭풍 클릭!🔥
                    """.trimIndent(),
                ),
            12 to
                listOf(
                    "",
                    """
                    과탑 DNA 깨어나는 중! 오늘도 앞서가요!🏆
                    """.trimIndent(),
                    """
                    절반 왔어요! 지금 텐션대로라면 과탑 각!🏅
                    """.trimIndent(),
                    """
                    마감 하루 전! 지금 텐션대로라면 과탑 각!🏅
                    """.trimIndent(),
                    """
                    마지막 1시간! 에너지 풀가동! 막판 스퍼트!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 에너지 풀가동! 막판 스퍼트!💥
                    """.trimIndent(),
                ),
            13 to
                listOf(
                    "",
                    """
                    공부도 분위기 있게, 오늘의 몰입 시작!🎶
                    """.trimIndent(),
                    """
                    절반 왔어요! 재즈와 함께 흐름을 이어가요.🎹
                    """.trimIndent(),
                    """
                    마감 하루 전! 재즈와 함께 흐름을 이어가요.🎹
                    """.trimIndent(),
                    """
                    마지막 1시간! 집중의 온도를 끌어올려 스퍼트!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 집중의 온도를 끌어올려 스퍼트!💥
                    """.trimIndent(),
                ),
            14 to
                listOf(
                    "",
                    """
                    따뜻한 재즈와 함께, 오늘도 한 줄씩 써봐요.🎷
                    """.trimIndent(),
                    """
                    절반 왔어요! 글의 향기가 깊어지고 있어요.☕️
                    """.trimIndent(),
                    """
                    마감 하루 전! 글의 향기가 깊어지고 있어요.☕️
                    """.trimIndent(),
                    """
                    마지막 1시간! 흐름을 살려 마지막까지 스퍼트!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 흐름을 살려 마지막까지 스퍼트!🔥
                    """.trimIndent(),
                ),
            15 to
                listOf(
                    "",
                    """
                    파도처럼 부드럽게, 근육을 깨우며 움직여봐요.🌊
                    """.trimIndent(),
                    """
                    절반 왔어요! 몸과의 대화에 더 집중해봐요.🧘
                    """.trimIndent(),
                    """
                    마감 하루 전! 몸과의 대화에 더 집중해봐요.🧘
                    """.trimIndent(),
                    """
                    마지막 1시간! 감각을 깨워 끝까지 스퍼트!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 감각을 깨워 끝까지 스퍼트!💥
                    """.trimIndent(),
                ),
            16 to
                listOf(
                    "",
                    """
                    재즈 선율과 함께, 오늘의 첫 코드를 채워봐요.🎺
                    """.trimIndent(),
                    """
                    절반 왔어요! 마지막까지 코드에 몰입해봐요.🚀
                    """.trimIndent(),
                    """
                    마감 하루 전! 마지막까지 코드에 몰입해봐요.🚀
                    """.trimIndent(),
                    """
                    마지막 1시간! 집중을 끌어올려 스퍼트 ON!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 집중을 끌어올려 스퍼트 ON!🔥
                    """.trimIndent(),
                ),
            17 to
                listOf(
                    "",
                    """
                    차분한 손길로, 오늘의 작업을 시작해볼까요?🎨
                    """.trimIndent(),
                    """
                    절반 왔어요! 디테일도 섬세하게 다듬어봐요.🖌️
                    """.trimIndent(),
                    """
                    마감 하루 전! 디테일도 섬세하게 다듬어봐요.🖌️
                    """.trimIndent(),
                    """
                    마지막 1시간! 완성도 높이며 스퍼트 올려봐요!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 완성도 높이며 스퍼트 올려봐요!💥
                    """.trimIndent(),
                ),
            18 to
                listOf(
                    "",
                    """
                    따뜻한 커피처럼, 과제도 한 모금씩 천천히!☕️
                    """.trimIndent(),
                    """
                    절반 왔어요! 낭만을 담아 끝까지 나아가요.📖
                    """.trimIndent(),
                    """
                    마감 하루 전! 낭만을 담아 끝까지 나아가요.📖
                    """.trimIndent(),
                    """
                    마지막 1시간! 깊이 몰입하며 끝까지 스퍼트!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 깊이 몰입하며 끝까지 스퍼트!🔥
                    """.trimIndent(),
                ),
            19 to
                listOf(
                    "",
                    """
                    도서관이 열렸어요. 오늘도 몰입해볼까요?👻
                    """.trimIndent(),
                    """
                    절반 왔어요! 남은 페이지도 조용히 돌파!🔕
                    """.trimIndent(),
                    """
                    마감 하루 전! 남은 페이지도 조용히 돌파!🔕
                    """.trimIndent(),
                    """
                    마지막 1시간! 소리 없이 완벽하게 스퍼트 ON!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 소리 없이 완벽하게 스퍼트 ON!🔥
                    """.trimIndent(),
                ),
            20 to
                listOf(
                    "",
                    """
                    한 단어씩, 오늘도 차분히 채워볼까요?📝
                    """.trimIndent(),
                    """
                    절반 왔어요! 한 줄씩 차곡차곡 마무리까지!📖
                    """.trimIndent(),
                    """
                    마감 하루 전! 한 줄씩 차곡차곡 마무리까지!📖
                    """.trimIndent(),
                    """
                    마지막 1시간! 조용하지만 강력한 막판 스퍼트!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 조용하지만 강력한 막판 스퍼트!💥
                    """.trimIndent(),
                ),
            21 to
                listOf(
                    "",
                    """
                    소리 없이 쌓이는 힘, 오늘도 시작해볼까요?🤫
                    """.trimIndent(),
                    """
                    절반 왔어요! 지금 템포 그대로 마지막까지!💪
                    """.trimIndent(),
                    """
                    마감 하루 전! 지금 템포 그대로 마지막까지!💪
                    """.trimIndent(),
                    """
                    마지막 1시간! 한 번 더, 묵묵히 스퍼트!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 한 번 더, 묵묵히 스퍼트!🔥
                    """.trimIndent(),
                ),
            22 to
                listOf(
                    "",
                    """
                    오늘도 묵묵히, 집중하며 한 줄씩 채워볼까요?🎧
                    """.trimIndent(),
                    """
                    절반 왔어요! 키보드 ASMR, 계속 이어가요.🧑‍💻
                    """.trimIndent(),
                    """
                    마감 하루 전! 키보드 ASMR, 계속 이어가요.🧑‍💻
                    """.trimIndent(),
                    """
                    마지막 1시간! 스퍼트 올려 완벽한 코드로!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 스퍼트 올려 완벽한 코드로!💥
                    """.trimIndent(),
                ),
            23 to
                listOf(
                    "",
                    """
                    조용히 몰입하며, 오늘도 디테일을 쌓아봐요.🖌️
                    """.trimIndent(),
                    """
                    절반 왔어요! 마무리까지 깊게 몰입해봐요.🧑‍💻
                    """.trimIndent(),
                    """
                    마감 하루 전! 마무리까지 깊게 몰입해봐요.🧑‍💻
                    """.trimIndent(),
                    """
                    마지막 1시간! 정교한 터치로 마무리 스퍼트!🔥
                    """.trimIndent(),
                    """
                    마지막 10분! 정교한 터치로 마무리 스퍼트!🔥
                    """.trimIndent(),
                ),
            24 to
                listOf(
                    "",
                    """
                    묵묵히 노력하면 A+로 돌아와요. 오늘도 출발!📚
                    """.trimIndent(),
                    """
                    절반 왔어요! A+을 향해 한 걸음 더!💯
                    """.trimIndent(),
                    """
                    마감 하루 전! A+을 향해 한 걸음 더!💯
                    """.trimIndent(),
                    """
                    마지막 1시간! 소리 없이 강하게 스퍼트 ON!💥
                    """.trimIndent(),
                    """
                    마지막 10분! 소리 없이 강하게 스퍼트 ON!💥
                    """.trimIndent(),
                ),
        )
}
