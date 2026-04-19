package com.example.task_test_work.service.task

import com.example.task_test_work.dto.task.TaskFilter
import com.example.task_test_work.dto.task.TaskRequest
import com.example.task_test_work.dto.task.TaskStatusUpdateRequest
import com.example.task_test_work.enums.task.TaskStatus
import com.example.task_test_work.exception.task.TaskNotFoundException
import com.example.task_test_work.model.task.Task
import com.example.task_test_work.repository.task.TaskRepository
import com.example.task_test_work.specification.task.TaskSpecificationBuilder
import com.example.task_test_work.util.specification.SqlCondition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TaskServiceTest {

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Mock
    private lateinit var taskSpecificationBuilder: TaskSpecificationBuilder

    @MockitoBean
    private lateinit var taskService: TaskService

    private val now = LocalDateTime.now()

    private val task = Task(
        id = 1L,
        title = "Test Task",
        description = "Description",
        status = TaskStatus.NEW,
        createdAt = now,
        updatedAt = now,
    )

    private val createRequest = TaskRequest(
        title = "Test Task",
        description = "Description"
    )

    private val updateRequest = TaskStatusUpdateRequest(status = TaskStatus.IN_PROGRESS)

    private val emptyCondition = SqlCondition(sql = "", params = emptyMap())


    @Test
    fun `createTask - successfully creates a task and returns TaskResponse`() {
        whenever(taskRepository.save(anyOrNull())).thenReturn(task)

        StepVerifier
            .create(taskService.createTask(createRequest))
            .assertNext { response ->
                assert(response.id == task.id)
                assert(response.title == task.title)
                assert(response.status == task.status)
            }
            .verifyComplete()
    }

    @Test
    fun `getTaskById - returns TaskResponse when task is found`() {
        whenever(taskRepository.findById(1L)).thenReturn(task)

        StepVerifier.create(taskService.getTaskById(1L))
            .assertNext { response ->
                assert(response.id == task.id)
                assert(response.title == task.title)
            }
            .verifyComplete()
    }

    @Test
    fun `getTaskById - throws TaskNotFoundException when task is not found`() {
        whenever(taskRepository.findById(999L)).thenReturn(null)

        StepVerifier
            .create(taskService.getTaskById(999L))
            .expectErrorMatches { error ->
                error is TaskNotFoundException && error.message?.contains("999") == true
            }
            .verify()
        }

    @Test
    fun `updateStatus - successfully updates task status`() {
        val updatedTask = task.copy(status = TaskStatus.IN_PROGRESS)

        whenever(taskRepository.findById(1L)).thenReturn(task)
        whenever(taskRepository.updateStatus(anyOrNull())).thenReturn(updatedTask)

        StepVerifier
            .create(taskService.updateStatus(1L, updateRequest))
            .assertNext { response ->
                assert(response.id == task.id)
                assert(response.status == TaskStatus.IN_PROGRESS)
            }
            .verifyComplete()
    }

    @Test
    fun `updateStatus - throws TaskNotFoundException when task is not found`() {
        whenever(taskRepository.findById(999L)).thenReturn(null)

        StepVerifier
            .create(taskService.updateStatus(999L, updateRequest))
            .expectErrorMatches { error ->
                error is TaskNotFoundException && error.message?.contains("999") == true
            }
            .verify()
    }

    @Test
    fun `deleteTask - successfully deletes a task and returns the number of affected rows`() {
        whenever(taskRepository.deleteById(1L)).thenReturn(1)

        StepVerifier
            .create(taskService.deleteTask(1L))
            .assertNext { affectedRows ->
                assert(affectedRows == 1)
            }
            .verifyComplete()
    }

    @Test
    fun `getTasks - returns a page of tasks with filtering and pagination`() {
        val filter = TaskFilter(status = TaskStatus.IN_PROGRESS)
        val pageable = PageRequest.of(0, 10)
        val taskPage = PageImpl(listOf(task), pageable, 1L)

        whenever(taskSpecificationBuilder.build(eq(filter))).thenReturn(emptyCondition)
        whenever(taskRepository.findAll(eq(emptyCondition), eq(pageable))).thenReturn(taskPage)

        StepVerifier
            .create(taskService.getTasks(filter, pageable))
            .assertNext { pageResponse ->
                assert(pageResponse.content.size == 1)
                assert(pageResponse.content.first().id == task.id)
                assert(pageResponse.totalElements == 1L)
                assert(pageResponse.page == 0)
                assert(pageResponse.size == 10)
            }
            .verifyComplete()
    }

    @Test
    fun `getTasks - returns an empty page when no tasks are found`() {
        val filter = TaskFilter(status = TaskStatus.DONE)
        val pageable = PageRequest.of(0, 10)
        val emptyPage = PageImpl(emptyList<Task>(), pageable, 0L)

        whenever(taskSpecificationBuilder.build(eq(filter))).thenReturn(emptyCondition)
        whenever(taskRepository.findAll(eq(emptyCondition), eq(pageable))).thenReturn(emptyPage)

        StepVerifier
            .create(taskService.getTasks(filter, pageable))
            .assertNext { pageResponse ->
                assert(pageResponse.content.isEmpty())
                assert(pageResponse.totalElements == 0L)
            }
            .verifyComplete()
    }
}