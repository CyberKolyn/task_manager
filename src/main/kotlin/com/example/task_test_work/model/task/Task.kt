package com.example.task_test_work.model.task

import com.example.task_test_work.enums.task.TaskStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tasks")
data class Task(
    @Id
    val id: Long? = null,

    val title: String,

    val description: String? = null,

    val status: TaskStatus = TaskStatus.NEW,

    val createdAt: LocalDateTime? = null,

    val updatedAt: LocalDateTime? = null,
)