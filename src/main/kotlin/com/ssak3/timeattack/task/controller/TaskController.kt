package com.ssak3.timeattack.task.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.common.dto.MessageResponse
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.UNAUTHORIZED_ACCESS
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.task.controller.dto.HomeTasksResponse
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateResponse
import com.ssak3.timeattack.task.controller.dto.TaskHoldOffRequest
import com.ssak3.timeattack.task.controller.dto.TaskResponse
import com.ssak3.timeattack.task.controller.dto.TaskStatusRequest
import com.ssak3.timeattack.task.controller.dto.TaskStatusResponse
import com.ssak3.timeattack.task.controller.dto.TaskUpdateRequest
import com.ssak3.timeattack.task.controller.dto.TaskWithRetrospectionResponse
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskResponse
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Task APIs")
@RestController
@RequestMapping("/v1/tasks")
class TaskController(
    private val taskService: TaskService,
) {
    @Operation(summary = "긴급 업무 생성", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/urgent")
    fun createUrgentTask(
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid urgentTaskRequest: UrgentTaskRequest,
    ): ResponseEntity<UrgentTaskResponse> {
        val createdUrgentTask = taskService.createUrgentTask(member, urgentTaskRequest)
        return ResponseEntity.ok(UrgentTaskResponse.from(createdUrgentTask))
    }

    @Operation(summary = "여유 있게 시작 작업 생성", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/scheduled")
    fun createScheduledTask(
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid scheduledTaskRequest: ScheduledTaskCreateRequest,
    ): ResponseEntity<ScheduledTaskCreateResponse> {
        val createdScheduledTask = taskService.createScheduledTask(member, scheduledTaskRequest)
        return ResponseEntity.ok(ScheduledTaskCreateResponse.fromTask(createdScheduledTask))
    }

    @Operation(summary = "작업 상태 변경", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PatchMapping("/{taskId}/status")
    fun changeStatus(
        @PathVariable(required = true) taskId: Long,
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid taskStatusRequest: TaskStatusRequest,
    ): ResponseEntity<TaskStatusResponse> {
        checkNotNull(member.id) { throw ApplicationException(UNAUTHORIZED_ACCESS) }
        val changedStatusTask = taskService.changeTaskStatus(taskId, member.id, taskStatusRequest.status)

        // 몰입 상태가 되면 응원 문구 푸시 알림 요청
        if (changedStatusTask.status == TaskStatus.FOCUSED) {
            taskService.requestSupportNotifications(taskId = taskId, memberId = member.id)
        }

        // 완료 상태가 되면 응원 문구 푸시 알림 비활성화
        if (changedStatusTask.status == TaskStatus.COMPLETE) {
            taskService.inactiveSupportNotifications(taskId = taskId, memberId = member.id)
        }

        return ResponseEntity.ok(TaskStatusResponse.from(changedStatusTask))
    }

    @Operation(summary = "오늘 할 작업 조회", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/today")
    fun findTodayTasks(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<List<TaskResponse>> {
        val todayTasks = taskService.findTodayTodoTasks(member)
        return ResponseEntity.ok(todayTasks.map { TaskResponse.fromTask(it) })
    }

    @Operation(
        summary = "이번 주 작업 목록 조회",
        description = "오늘 할 일 목록을 제외하고 내일부터 일요일까지 할 일 목록 조회",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
    )
    @GetMapping("/current-week")
    fun getCurrentWeekTasks(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<List<TaskResponse>> {
        val taskResponseList = taskService.getTodoTasksForRestOfCurrentWeek(member).map { TaskResponse.fromTask(it) }

        return ResponseEntity.ok(taskResponseList)
    }

    @Operation(
        summary = "전체 할일 조회",
        description = "조회 시점을 기준으로 마감시간이 지나지 않은 일들을 조회",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
    )
    @GetMapping("/all-todos")
    fun findAllTodos(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<List<TaskResponse>> {
        val taskResponseList = taskService.findAllTodoTasks(member).map { TaskResponse.fromTask(it) }

        return ResponseEntity.ok(taskResponseList)
    }

    @Operation(summary = "작업 조회", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/{taskId}")
    fun findTask(
        @Parameter(description = "작업 ID")
        @PathVariable(required = true)
        @Positive taskId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<TaskResponse> {
        val task = taskService.findTaskByIdAndMember(member, taskId)
        return ResponseEntity.ok(TaskResponse.fromTask(task))
    }

    @Operation(
        summary = "작업 삭제",
        description = "사용자의 작업 중 요청받은 작업을 삭제합니다.",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
    )
    @DeleteMapping("/{taskId}")
    fun removeTask(
        @Parameter(description = "작업 ID")
        @PathVariable(required = true)
        @Positive taskId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<MessageResponse> {
        taskService.removeTask(member, taskId)
        return ResponseEntity.ok(MessageResponse("Task removed successfully"))
    }

    // TODO: find -> get
    @Operation(
        summary = "홈 화면 작업 조회",
        description = "홈 화면에 표시할 수 있는 모든 작업 목록을 조회합니다.",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
    )
    @GetMapping("/home")
    fun findHomeTasks(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<HomeTasksResponse> {
        val todayTodoTasks = taskService.findTodayTodoTasks(member)
        val weeklyTodoTasks = taskService.getTodoTasksForRestOfCurrentWeek(member)
        val allTodoTasks = taskService.findAllTodoTasks(member)
        val missionEscapeTask = taskService.getAbandonedOrIgnoredTasks(member)
        return ResponseEntity.ok(
            HomeTasksResponse.fromTasks(todayTodoTasks, weeklyTodoTasks, allTodoTasks, missionEscapeTask),
        )
    }

    @Operation(summary = "작업 리마인더 설정", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PatchMapping("/{taskId}/hold-off")
    fun holdOffTask(
        @AuthenticationPrincipal member: Member,
        @PathVariable(required = true) taskId: Long,
        @RequestBody @Valid taskHoldOffRequest: TaskHoldOffRequest,
    ): ResponseEntity<MessageResponse> {
        taskService.holdOffTask(taskId, member, taskHoldOffRequest)
        return ResponseEntity.ok(MessageResponse("Task hold-off successfully"))
    }

    @Operation(
        summary = "작업 수정",
        description = "사용자의 작업 중 요청받은 작업을 수정합니다.",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
    )
    @PatchMapping("/{taskId}")
    fun updateTask(
        @Parameter(description = "작업 ID")
        @PathVariable(required = true)
        @Positive taskId: Long,
        @RequestBody @Valid taskUpdateRequest: TaskUpdateRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<TaskResponse> {
        taskUpdateRequest.validateRequest()
        val updatedTask = taskService.updateTask(member, taskId, taskUpdateRequest)
        return ResponseEntity.ok(TaskResponse.fromTask(updatedTask))
    }

    @Operation(summary = "회고와 함께 작업 조회(마이페이지용)", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/{taskId}/retrospection")
    fun getRetrospection(
        @Parameter(description = "작업 ID")
        @PathVariable(required = true)
        @Positive taskId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<TaskWithRetrospectionResponse> {
        val (task, retrospection) = taskService.getTaskWithRetrospection(member, taskId)

        val response =
            retrospection?.let { TaskWithRetrospectionResponse.fromTaskAndRetrospection(task, it) }
                ?: TaskWithRetrospectionResponse.fromTaskOnly(task)

        return ResponseEntity.ok(response)
    }
}
