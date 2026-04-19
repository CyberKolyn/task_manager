package com.example.task_test_work.controller.task

import com.example.task_test_work.dto.task.TaskFilter
import com.example.task_test_work.dto.task.TaskRequest
import com.example.task_test_work.dto.task.TaskResponse
import com.example.task_test_work.dto.task.TaskStatusUpdateRequest
import com.example.task_test_work.service.task.TaskService
import com.example.task_test_work.util.pagination.PageResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService,
) {

    @GetMapping
    fun all(
        @ModelAttribute filter: TaskFilter = TaskFilter(),
        pageable: Pageable
    ): Mono<PageResponse<TaskResponse>> =
        taskService.getTasks(filter, pageable)

    @GetMapping("{id}")
    fun oneById(@PathVariable id: Long): Mono<TaskResponse> = taskService.getTaskById(id)

    @PostMapping
    fun create(@RequestBody @Valid dto: TaskRequest): Mono<TaskResponse>  = taskService.createTask(dto)

    @PatchMapping("{id}/status")
    fun update(@PathVariable id: Long, @RequestBody dto: TaskStatusUpdateRequest): Mono<TaskResponse>
        = taskService.updateStatus(id, dto)

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = taskService.deleteTask(id)

}