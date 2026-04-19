package com.example.task_test_work.util.pagination

data class PageResponse<T>(
    val content: List<T>,

    val page: Int,

    val size: Int,

    val totalElements: Long,

    val totalPages: Int,
)