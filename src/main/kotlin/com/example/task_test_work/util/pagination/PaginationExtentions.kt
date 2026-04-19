package com.example.task_test_work.util.pagination

import org.springframework.data.domain.Page

fun <T> Page<T>.toPageResponse(): PageResponse<T> = PageResponse(
    content = content,
    page = number,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages
)