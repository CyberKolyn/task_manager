package com.example.task_test_work.mapper.task

import com.example.task_test_work.dto.task.TaskRequest
import com.example.task_test_work.dto.task.TaskResponse
import com.example.task_test_work.model.task.Task

fun TaskRequest.toModal() = Task(
    title = title,
    description = description
)

fun Task.toResponse() = TaskResponse(
    id = id!!,
    title = title,
    description = description,
    status = status,
    createdAt = createdAt!!,
    updatedAt = updatedAt!!,
)