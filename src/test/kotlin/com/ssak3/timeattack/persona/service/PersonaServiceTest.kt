package com.ssak3.timeattack.persona.service

import com.ssak3.timeattack.IntegrationTest
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
class PersonaServiceTest(
    @Autowired private val personaService: PersonaService,
    @Autowired private val personaRepository: PersonaRepository,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val taskRepository: TaskRepository,
) : DescribeSpec({
        describe("PersonaService") {
            lateinit var member: Member
            lateinit var taskTypeEntity1: TaskTypeEntity
            lateinit var taskTypeEntity2: TaskTypeEntity
            lateinit var taskModeEntity1: TaskModeEntity
            lateinit var taskModeEntity2: TaskModeEntity

            beforeEach {
                member = Member.fromEntity(memberRepository.save(Fixture.createMember(id = null).toEntity()))

                taskTypeEntity1 = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "프로그래밍"))
                taskTypeEntity2 = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "글쓰기"))

                taskModeEntity1 = taskModeRepository.saveAndFlush(TaskModeEntity(name = "긴급한"))
                taskModeEntity2 = taskModeRepository.saveAndFlush(TaskModeEntity(name = "신나는"))

                val personaEntity1 =
                    personaRepository.saveAndFlush(
                        PersonaEntity(
                            name = "폭주 기관차",
                            taskType = taskTypeEntity1,
                            taskMode = taskModeEntity1,
                        ),
                    )

                val personaEntity2 =
                    personaRepository.saveAndFlush(
                        PersonaEntity(
                            name = "타오르는 만년필",
                            taskType = taskTypeEntity2,
                            taskMode = taskModeEntity2,
                        ),
                    )

                val persona1 = Persona.fromEntity(personaEntity1)
                val persona2 = Persona.fromEntity(personaEntity2)

                val task1 =
                    Fixture.createTask(
                        id = null,
                        member = member,
                        persona = persona1,
                    )

                val task2 =
                    Fixture.createTask(
                        id = null,
                        member = member,
                        persona = persona2,
                    )

                taskRepository.saveAll(listOf(task1.toEntity(), task2.toEntity()))
            }

            afterEach {
                personaRepository.deleteAll()
                taskModeRepository.deleteAll()
                taskTypeRepository.deleteAll()
                memberRepository.deleteAll()
            }

            context("getAllPersonas 호출 시") {
                it("페르소나 목록을 올바르게 반환한다") {
                    val memberId = checkNotNull(member.id, "MemberId")
                    val personas = personaService.getAllPersonas(memberId)

                    personas.size shouldBe 2

                    personas.map { it.name }.containsAll(listOf("폭주 기관차", "타오르는 만년필")) shouldBe true
                }

                it("페르소나가 없는 경우 빈 목록을 반환한다") {
                    personaRepository.deleteAll()
                    val memberId = checkNotNull(member.id, "MemberId")

                    val personas = personaService.getAllPersonas(memberId)

                    personas.shouldBeEmpty()
                }
            }
        }
    })
