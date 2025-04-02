package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.task.client.GoogleCloudProperties
import com.ssak3.timeattack.task.client.YoutubeDataClient
import com.ssak3.timeattack.task.domain.ImmersionTask
import org.springframework.stereotype.Service

@Service
class ImmersionTaskService(
    private val taskService: TaskService,
    private val subtaskService: SubtaskService,
    private val youtubeDataClient: YoutubeDataClient,
    private val googleCloudProperties: GoogleCloudProperties,
) {
    fun getImmersionTasks(member: Member): List<ImmersionTask> {
        val activeTasks = taskService.getActiveTasks(member)
        val immersionTasks: List<ImmersionTask> =
            activeTasks.map { task ->
                checkNotNull(task.id, "taskId")
                val keyword = task.persona.taskKeywordsCombination.keyword
                val playlistIds = getPlayListIds(keyword)
                val subtask = subtaskService.getAll(task)
                ImmersionTask(
                    taskId = task.id,
                    taskName = task.name,
                    dueDatetime = task.dueDatetime,
                    personaId = task.persona.id,
                    personaName = task.persona.name,
                    subtasks = subtask,
                    playlistIds = playlistIds,
                )
            }

        return immersionTasks
    }

    private fun getPlayListIds(keyword: String): List<String> {
        val response =
            youtubeDataClient.getVideos(
                key = googleCloudProperties.apiKey,
                searchKeyword = "$keyword 플레이리스트",
            )

        return response.items.map { it.id.videoId }
    }
}
