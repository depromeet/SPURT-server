package com.ssak3.timeattack.task.service

import com.ninjasquad.springmockk.MockkBean
import com.ssak3.timeattack.IntegrationTest
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.notifications.service.PushNotificationListener
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.TaskUpdateRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import com.ssak3.timeattack.task.service.events.DeleteTaskNotificationEvent
import com.ssak3.timeattack.task.service.events.TriggerActionNotificationUpdateEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.event.ApplicationEvents
import java.time.LocalDateTime

@IntegrationTest
class TaskServiceTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val taskRepository: TaskRepository,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val personaRepository: PersonaRepository,
) : DescribeSpec() {
    @Autowired lateinit var events: ApplicationEvents

    @MockkBean lateinit var pushNotificationListener: PushNotificationListener

    init {
        lateinit var member: Member
        lateinit var persona: Persona
        lateinit var now: LocalDateTime

        beforeEach {
            member = Member.fromEntity(memberRepository.save(Fixture.createMember(id = null).toEntity()))
            val taskTypeEntity = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "프로그래밍"))
            val taskModeEntity = taskModeRepository.saveAndFlush(TaskModeEntity(name = "긴급한"))
            persona =
                Persona.fromEntity(
                    personaRepository.save(
                        PersonaEntity(
                            name = "Urgent Programmer",
                            taskType = taskTypeEntity,
                            taskMode = taskModeEntity,
                        ),
                    ),
                )
            now = LocalDateTime.now()
        }

        afterEach {
            taskRepository.deleteAll()
            personaRepository.deleteAll()
            taskModeRepository.deleteAll()
            taskTypeRepository.deleteAll()
            memberRepository.deleteAll()
        }

        describe("Scheduled Task 수정 시") {
            lateinit var task: Task

            beforeEach {
                val taskToSave = Fixture.createScheduledTaskWithNow(now, id = null, member = member, persona = persona)
                task = Task.fromEntity(taskRepository.save(taskToSave.toEntity()))
            }

            context("소요시간 수정에 대해") {
                it("유효한 값일 경우 정상적으로 수정된다.") {
                    // given
                    val taskUpdateRequest =
                        TaskUpdateRequest(
                            name = "modified task",
                            estimatedTime = 70,
                            triggerActionAlarmTime = now.plusMinutes(70),
                            triggerAction = "modified trigger action",
                            isUrgent = false,
                        )

                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updatedTask = taskRepository.findById(taskId).get()
                    updatedTask.shouldNotBeNull()
                    updatedTask.name shouldBe "modified task"
                    updatedTask.triggerActionAlarmTime shouldBe now.plusMinutes(70)
                    updatedTask.dueDatetime shouldBe now.plusMinutes(180)
                    updatedTask.estimatedTime shouldBe 70
                    updatedTask.triggerAction shouldBe "modified trigger action"
                    updatedTask.status shouldBe TaskStatus.BEFORE
                }
            }

            context("즉시 몰입 중으로 바뀌지 않는 마감시간 수정에 대해") {
                it("유효한 값일 경우 정상적으로 수정된다.") {
                    // given
                    val taskUpdateRequest =
                        TaskUpdateRequest(
                            name = "modified task",
                            dueDatetime = now.plusMinutes(150),
                            triggerActionAlarmTime = now.plusMinutes(70),
                            triggerAction = "modified trigger action",
                            isUrgent = false,
                        )

                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updatedTask = taskRepository.findById(taskId).get()
                    updatedTask.shouldNotBeNull()
                    updatedTask.name shouldBe "modified task"
                    updatedTask.triggerActionAlarmTime shouldBe now.plusMinutes(70)
                    updatedTask.dueDatetime shouldBe now.plusMinutes(150)
                    updatedTask.estimatedTime shouldBe 60
                    updatedTask.triggerAction shouldBe "modified trigger action"
                    updatedTask.status shouldBe TaskStatus.BEFORE
                }
            }

            context("즉시 몰입 중으로 바뀌어야 하는 마감시간 수정에 대해") {
                // given
                lateinit var taskUpdateRequest: TaskUpdateRequest

                beforeEach {
                    taskUpdateRequest =
                        TaskUpdateRequest(
                            name = "modified task",
                            dueDatetime = now.plusMinutes(50),
                            triggerAction = "modified trigger action",
                            isUrgent = true,
                        )
                }

                it("유효한 값일 경우 정상적으로 수정된다.") {
                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updatedTask = taskRepository.findById(taskId).get()
                    updatedTask.shouldNotBeNull()
                    updatedTask.name shouldBe "modified task"
                    updatedTask.triggerActionAlarmTime shouldBe null
                    updatedTask.dueDatetime shouldBe now.plusMinutes(50)
                    updatedTask.estimatedTime shouldBe 60
                    updatedTask.triggerAction shouldBe "modified trigger action"
                    updatedTask.status shouldBe TaskStatus.FOCUSED
                }

                it("기존 알림 삭제 요청을 한다.") {
                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val deleteTaskNotificationEvents = events.stream(DeleteTaskNotificationEvent::class.java).toList()
                    deleteTaskNotificationEvents.size shouldBe 1

                    val deleteTaskNotificationEvent = deleteTaskNotificationEvents[0]
                    deleteTaskNotificationEvent.memberId shouldBe member.id
                    deleteTaskNotificationEvent.taskId shouldBe taskId
                }

                it("기존 알림 삭제 요청 EventListener 에서 예외가 발생해도 정상적으로 작업이 수정된다.") {
                    // given
                    val taskId = checkNotNull(task.id, "task.id")
                    every { pushNotificationListener.deleteNotifications(any()) } throws RuntimeException()

                    // when
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updatedTask = taskRepository.findById(taskId).get()
                    updatedTask.shouldNotBeNull()
                    updatedTask.name shouldBe "modified task"
                    updatedTask.triggerActionAlarmTime shouldBe null
                    updatedTask.dueDatetime shouldBe now.plusMinutes(50)
                    updatedTask.estimatedTime shouldBe 60
                    updatedTask.triggerAction shouldBe "modified trigger action"
                    updatedTask.status shouldBe TaskStatus.FOCUSED
                }
            }

            context("작은 행동 알림 업데이트가 있을 경우") {
                lateinit var taskUpdateRequest: TaskUpdateRequest

                beforeEach {
                    // given
                    taskUpdateRequest =
                        TaskUpdateRequest(
                            name = "modified task",
                            dueDatetime = now.plusMinutes(240),
                            triggerActionAlarmTime = now.plusMinutes(120),
                            triggerAction = "modified trigger action",
                            isUrgent = false,
                        )
                }

                it("작은 행동 알림 업데이트를 요청한다.") {
                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updateEvents = events.stream(TriggerActionNotificationUpdateEvent::class.java).toList()
                    updateEvents.size shouldBe 1

                    val updateEvent = updateEvents[0]
                    updateEvent.memberId shouldBe member.id
                    updateEvent.taskId shouldBe taskId
                    updateEvent.alarmTime shouldBe now.plusMinutes(120)
                }

                it("작은 행동 알림 업데이트 EventListener 에서 예외가 발생해도 정상적으로 작업이 수정된다.") {
                    // given
                    val taskId = checkNotNull(task.id, "task.id")
                    every { pushNotificationListener.updateNotification(any()) } throws RuntimeException()

                    // when
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updatedTask = taskRepository.findById(taskId).get()
                    updatedTask.shouldNotBeNull()
                    updatedTask.name shouldBe "modified task"
                    updatedTask.triggerActionAlarmTime shouldBe now.plusMinutes(120)
                    updatedTask.dueDatetime shouldBe now.plusMinutes(240)
                    updatedTask.estimatedTime shouldBe 60
                    updatedTask.triggerAction shouldBe "modified trigger action"
                    updatedTask.status shouldBe TaskStatus.BEFORE
                }
            }

            context("작은 행동 알림 업데이트가 있지 않을 경우") {
                it("작은 행동 알림 업데이트를 요청하지 않는다.") {
                    // given
                    val taskUpdateRequest =
                        TaskUpdateRequest(
                            name = "modified task",
                            triggerAction = "modified trigger action",
                            isUrgent = false,
                        )

                    // when
                    val taskId = checkNotNull(task.id, "task.id")
                    taskService.updateTask(member, taskId, taskUpdateRequest)

                    // then
                    val updateEvents = events.stream(TriggerActionNotificationUpdateEvent::class.java).toList()
                    updateEvents.size shouldBe 0
                }
            }
        }

        describe("완료된 일 조회 시") {
            var memberId: Long = 0
            beforeEach {
                member = Member.fromEntity(memberRepository.save(Fixture.createMember(id = null).toEntity()))
                memberId = checkNotNull(member.id, "MemberId")

                val task1 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 1",
                        status = TaskStatus.COMPLETE,
                        member = member,
                        updatedAt = LocalDateTime.now().minusDays(1),
                    )
                val task2 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 2",
                        status = TaskStatus.COMPLETE,
                        member = member,
                        updatedAt = LocalDateTime.now(),
                    )
                val task3 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 3",
                        status = TaskStatus.FOCUSED,
                        member = member,
                        updatedAt = LocalDateTime.now(),
                    )

                taskRepository.saveAll(
                    listOf(
                        task1.toEntity(),
                        task2.toEntity(),
                        task3.toEntity(),
                    ),
                )
            }

            it("완료된 작업만 완료시간 역순으로 조회한다") {
                val result = taskService.getCompletedTasksOrderByCompletedTimeDesc(memberId)

                result.size shouldBe 2
                result[0].name shouldBe "Task 2"
                result[1].name shouldBe "Task 1"

                // FOCUSED 상태 태스크는 포함되지 않음
                result.any { it.name == "Task 3" } shouldBe false
            }

            it("완료된 작업이 없으면 빈 목록을 반환한다") {
                taskRepository.deleteAll()

                val result = taskService.getCompletedTasksOrderByCompletedTimeDesc(memberId)

                result.shouldBeEmpty()
            }
        }

        describe("미룬 일 조회 시") {
            var memberId: Long = 0
            beforeEach {
                member = Member.fromEntity(memberRepository.save(Fixture.createMember(id = null).toEntity()))
                memberId = checkNotNull(member.id, "MemberId")

                val task1 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 1",
                        status = TaskStatus.PROCRASTINATING,
                        member = member,
                        dueDatetime = LocalDateTime.now().minusDays(1),
                    )
                val task2 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 2",
                        status = TaskStatus.FAIL,
                        member = member,
                        dueDatetime = LocalDateTime.now(),
                    )
                val task3 =
                    Fixture.createTask(
                        id = null,
                        name = "Task 3",
                        status = TaskStatus.FOCUSED,
                        member = member,
                        dueDatetime = LocalDateTime.now(),
                    )

                taskRepository.saveAll(
                    listOf(
                        task1.toEntity(),
                        task2.toEntity(),
                        task3.toEntity(),
                    ),
                )
            }

            it("미룬 작업만 마감일 역순으로 조회한다") {
                val result = taskService.getProcrastinatedTasksOrderByDueDateDesc(memberId)

                result.size shouldBe 2
                result[0].name shouldBe "Task 2"
                result[1].name shouldBe "Task 1"

                // FOCUSED 상태 태스크는 포함되지 않음
                result.any { it.name == "Task 3" } shouldBe false
            }

            it("미룬 작업이 없으면 빈 목록을 반환한다") {
                taskRepository.deleteAll()

                val result = taskService.getCompletedTasksOrderByCompletedTimeDesc(memberId)

                result.shouldBeEmpty()
            }
        }
    }
}
