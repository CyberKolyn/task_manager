package com.example.task_test_work.util.specification

fun List<String>.toWhereAndClause(): String =
    joinToString(separator = " AND ", prefix = "AND ").takeIf { isNotEmpty() } ?: ""