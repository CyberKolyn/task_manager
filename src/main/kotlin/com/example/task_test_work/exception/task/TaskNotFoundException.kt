package com.example.task_test_work.exception.task

class TaskNotFoundException(id: Long) : NoSuchElementException("Task not found [id: $id]")