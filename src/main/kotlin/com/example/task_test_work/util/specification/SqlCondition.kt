package com.example.task_test_work.util.specification

data class SqlCondition(
    val sql: String,
    val params: Map<String, Any>
)
