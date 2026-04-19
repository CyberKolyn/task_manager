package com.example.task_test_work.dto.task

import com.example.task_test_work.enums.task.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(

    val id: Long,

    val title: String,

    val description: String?,

    val status: TaskStatus,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime,

)
