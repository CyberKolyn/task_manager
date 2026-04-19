package com.example.task_test_work.specification.task

import com.example.task_test_work.dto.task.TaskFilter
import com.example.task_test_work.util.specification.Specification
import com.example.task_test_work.util.specification.SqlCondition
import com.example.task_test_work.util.specification.toWhereAndClause
import org.springframework.stereotype.Component

@Component
class TaskSpecificationBuilder : Specification<TaskFilter> {

    override fun build(filter: TaskFilter): SqlCondition {
        val conditions = mutableListOf<String>()
        val params = mutableMapOf<String, Any>()

        filter.status?.let {
            conditions += "status = :status"
            params["status"] = it.name
        }

        return SqlCondition(conditions.toWhereAndClause(), params)
    }
}