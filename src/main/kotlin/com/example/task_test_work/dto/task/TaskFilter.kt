package com.example.task_test_work.dto.task

import com.example.task_test_work.enums.task.TaskStatus

data class TaskFilter(
    val status: TaskStatus? = null,
)