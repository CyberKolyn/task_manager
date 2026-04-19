package com.example.task_test_work.service.task

import com.example.task_test_work.dto.task.TaskFilter
import com.example.task_test_work.dto.task.TaskRequest
import com.example.task_test_work.dto.task.TaskResponse
import com.example.task_test_work.dto.task.TaskStatusUpdateRequest
import com.example.task_test_work.exception.task.TaskNotFoundException
import com.example.task_test_work.mapper.task.toModal
import com.example.task_test_work.mapper.task.toResponse
import com.example.task_test_work.repository.task.TaskRepository
import com.example.task_test_work.specification.task.TaskSpecificationBuilder
import com.example.task_test_work.util.pagination.PageResponse
import com.example.task_test_work.util.pagination.toPageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskSpecificationBuilder: TaskSpecificationBuilder,
) {

    fun getTasks(filter: TaskFilter, pageable: Pageable): Mono<PageResponse<TaskResponse>> =
        Mono.fromCallable {
            val condition = taskSpecificationBuilder.build(filter)
            taskRepository.findAll(condition, pageable).map { it.toResponse() }.toPageResponse()
        }
        .subscribeOn(Schedulers.boundedElastic())

    fun getTaskById(id: Long):  Mono<TaskResponse> =
        Mono.fromCallable {
            taskRepository.findById(id)?.toResponse() ?: throw TaskNotFoundException(id)
        }
            .subscribeOn(Schedulers.boundedElastic())


    fun createTask(dto: TaskRequest): Mono<TaskResponse> =
        Mono.fromCallable {
            taskRepository
                .save(dto.toModal())
                .toResponse()
        }
        .subscribeOn(Schedulers.boundedElastic())

    fun updateStatus(id: Long, dto: TaskStatusUpdateRequest): Mono<TaskResponse> =
        Mono.fromCallable {
            val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)

            val updatingTaskStatus = task.copy(status = dto.status)

            taskRepository.updateStatus(updatingTaskStatus).toResponse()
        }
        .subscribeOn(Schedulers.boundedElastic())


    fun deleteTask(id: Long): Mono<Int> =
        Mono.fromCallable {
            taskRepository.deleteById(id)
        }
        .subscribeOn(Schedulers.boundedElastic())


}