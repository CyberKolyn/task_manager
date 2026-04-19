package com.example.task_test_work.util.specification

fun interface Specification<F> {

    fun build(filter: F): SqlCondition
}