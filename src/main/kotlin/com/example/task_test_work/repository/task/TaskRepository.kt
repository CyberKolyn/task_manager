package com.example.task_test_work.repository.task

import com.example.task_test_work.model.task.Task
import com.example.task_test_work.util.specification.SqlCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Repository
class TaskRepository(
    private val jdbcClient: JdbcClient
) {

    companion object{
        private const val ALL_RETURNING_FIELDS = "id, title, description, status, created_at, updated_at"
    }

    fun findAll(condition: SqlCondition, pageable: Pageable): Page<Task> {

        val tasks = jdbcClient.sql(
            """
            SELECT *
            FROM public.tasks
            WHERE 1 = 1
            ${condition.sql}
            ORDER BY id
            LIMIT :limit OFFSET :offset
            """
        )
            .param("limit", pageable.pageSize)
            .param("offset", pageable.pageNumber * pageable.pageSize)
            .params(condition.params)
            .query(Task::class.java)
            .list()
            .filterNotNull()

        val total = jdbcClient.sql(
            """
            SELECT count(*) 
            FROM public.tasks
            WHERE 1 = 1
            ${condition.sql}
            """
        )
            .params(condition.params)
            .query(Int::class.java)
            .single()

        return PageImpl(
            tasks,
            PageRequest.of(pageable.pageNumber, pageable.pageSize),
            total.toLong()
        )
    }

    fun findById(id: Long): Task? =
        jdbcClient.sql(
            """
            SELECT * FROM public.tasks WHERE id = :id LIMIT 1
            """
        )
            .param("id", id)
            .query(Task::class.java)
            .optional()
            .orElse(null)

    fun save(task: Task): Task {
        if (task.id != null) {
            throw Exception("Attempt to assign manual ID assignment")
        }

        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)

        return jdbcClient.sql(
            """
            INSERT INTO public.tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :created_at, :updated_at)
            RETURNING $ALL_RETURNING_FIELDS
            """
        )
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("created_at", now)
            .param("updated_at", now)
            .query(Task::class.java)
            .single()
    }

    fun updateStatus(task: Task): Task =
        jdbcClient.sql(
            """
            UPDATE public.tasks 
            SET status = :status, 
                updated_at = :updatedAt 
            WHERE id = :id
            RETURNING $ALL_RETURNING_FIELDS
            """
        )
            .param("id", task.id)
            .param("status", task.status.name)
            .param("updatedAt", LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
            .query(Task::class.java)
            .single()

    fun deleteById(id: Long): Int =
        jdbcClient
            .sql("""DELETE FROM public.tasks WHERE id = :id""")
            .param("id", id)
            .update()
}