package com.example.task_test_work.controller.task

import com.example.task_test_work.dto.task.TaskRequest
import com.example.task_test_work.dto.task.TaskResponse
import com.example.task_test_work.dto.task.TaskStatusUpdateRequest
import com.example.task_test_work.enums.task.TaskStatus
import com.example.task_test_work.service.task.TaskService
import com.example.task_test_work.util.pagination.PageResponse
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import kotlin.test.Test

@WebFluxTest(TaskController::class)
class TaskControllerTest {

    @MockitoBean
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private val now = LocalDateTime.now()

    private val taskResponse = TaskResponse(
        id = 1L,
        title = "Test Task",
        description = "Description",
        status = TaskStatus.NEW,
        createdAt = now,
        updatedAt = now,
    )

    @Test
    fun `createTask - returns 200 and TaskResponse when request is valid`() {
        val request = TaskRequest(title = "Test Task", description = "Description")

        whenever(taskService.createTask(anyOrNull())).thenReturn(Mono.just(taskResponse))

        webTestClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1L)
            .jsonPath("$.title").isEqualTo("Test Task")
            .jsonPath("$.status").isEqualTo("NEW")
    }

    @Test
    fun `createTask - returns 400 when title is too short`() {
        val request = mapOf("title" to "ab")

        webTestClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.message").value<String> { it.contains("title") }
    }

    @Test
    fun `createTask - returns 400 when title is missing`() {
        val request = mapOf("description" to "some desc")

        webTestClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
    }

    @Test
    fun `getTaskById - returns 200 and TaskResponse when task exists`() {
        whenever(taskService.getTaskById(1L)).thenReturn(Mono.just(taskResponse))

        webTestClient.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1L)
            .jsonPath("$.title").isEqualTo("Test Task")
    }

    @Test
    fun `getTaskById - returns 404 when task does not exist`() {
        whenever(taskService.getTaskById(999L))
            .thenReturn(Mono.error(NoSuchElementException("Task with id 999 not found")))

        webTestClient.get()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.message").value<String> { it.contains("999") }
    }

    @Test
    fun `updateStatus - returns 200 and updated TaskResponse`() {
        val updated = taskResponse.copy(status = TaskStatus.IN_PROGRESS)
        val request = TaskStatusUpdateRequest(status = TaskStatus.IN_PROGRESS)

        whenever(taskService.updateStatus(eq(1L), anyOrNull())).thenReturn(Mono.just(updated))

        webTestClient.patch()
            .uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("IN_PROGRESS")
    }

    @Test
    fun `updateStatus - returns 404 when task does not exist`() {
        whenever(taskService.updateStatus(eq(999L), anyOrNull()))
            .thenReturn(Mono.error(NoSuchElementException("Task with id 999 not found")))

        webTestClient.patch()
            .uri("/api/tasks/999/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TaskStatusUpdateRequest(status = TaskStatus.IN_PROGRESS))
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.message").value<String> { it.contains("999") }
    }

    @Test
    fun `deleteTask - returns 204 when task is deleted`() {
        whenever(taskService.deleteTask(1L)).thenReturn(Mono.just(1))

        webTestClient.delete()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `deleteTask - returns 404 when task does not exist`() {
        whenever(taskService.deleteTask(999L))
            .thenReturn(Mono.error(NoSuchElementException("Task with id 999 not found")))

        webTestClient.delete()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.message").value<String> { it.contains("999") }
    }

    @Test
    fun `getTasks - returns 200 and page of tasks`() {
        val pageResponse = PageResponse(
            content = listOf(taskResponse),
            totalElements = 1L,
            page = 0,
            size = 10,
            totalPages = 1,
        )

        whenever(taskService.getTasks(anyOrNull(), anyOrNull())).thenReturn(Mono.just(pageResponse))

        webTestClient.get()
            .uri("/api/tasks?page=0&size=10")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content.length()").isEqualTo(1)
            .jsonPath("$.totalElements").isEqualTo(1)
            .jsonPath("$.page").isEqualTo(0)
            .jsonPath("$.size").isEqualTo(10)
    }
}