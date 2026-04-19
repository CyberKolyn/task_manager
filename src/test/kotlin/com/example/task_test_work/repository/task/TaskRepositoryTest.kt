package com.example.task_test_work.repository.task

import com.example.task_test_work.dto.task.TaskFilter
import com.example.task_test_work.enums.task.TaskStatus
import com.example.task_test_work.model.task.Task
import com.example.task_test_work.specification.task.TaskSpecificationBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.NONE)
@Transactional
class TaskRepositoryTest@Autowired constructor(
    private val taskRepository: TaskRepository,
    private val taskSpecificationBuilder: TaskSpecificationBuilder,
) {
    @Test
    fun `createTask - successfully creates task and returns non-null id`() {
        val saved = createTask("create")

        assertNotNull(saved.id)
        assertEquals("test-create", saved.title)
        assertEquals("desc-create", saved.description)
        assertEquals(TaskStatus.NEW, saved.status)
    }

    @Test
    fun `findById - returns task when task exists`() {
        val saved = createTask("foundTaskById")

        assertNotNull(saved.id)

        val foundTaskById = taskRepository.findById(saved.id!!)

        assertNotNull(foundTaskById)
        assertEquals(foundTaskById.id, saved.id)
        assertEquals("test-foundTaskById", foundTaskById.title)
        assertEquals("desc-foundTaskById", foundTaskById.description)

    }

    @Test
    fun `deleteTask - successfully deletes task and findById returns null`() {
        val saved = createTask("delete")

        assertNotNull(saved.id)

        val deleted = taskRepository.deleteById(saved.id!!)
        assert(deleted > 0)

        val foundTaskById = taskRepository.findById(saved.id!!)
        assertNull(foundTaskById)
    }

    @Test
    fun `updateStatus - successfully updates task status and updatedAt is refreshed`() {
        val saved = createTask("update")

        assertNotNull(saved.id)

        val newStatusTask = saved.copy(status = TaskStatus.IN_PROGRESS)

        val updated = taskRepository.updateStatus(newStatusTask)
        assertEquals(updated.id, saved.id)

        assertNotNull(updated)
        assertEquals(updated.status, TaskStatus.IN_PROGRESS)
        updated!!.updatedAt?.let { assert(it > saved!!.updatedAt) }
    }

    @Test
    fun `findAll - returns correct page size when paginating`() {
        val taskSize = 20L
        val pageSize = 5

        (1..taskSize).forEach {
            createTask("$it")
        }

        val condition = taskSpecificationBuilder.build(TaskFilter())
        val tasksFirstPage = taskRepository.findAll(condition, PageRequest.of(0, pageSize))

        assertEquals(tasksFirstPage.content.size, pageSize)

        val tasksSecondPage = taskRepository.findAll(condition, PageRequest.of(1, pageSize))
        assertEquals(tasksSecondPage.content.size, pageSize)

        val tasksThirdPage = taskRepository.findAll(condition, PageRequest.of(2, pageSize))
        assertEquals(tasksThirdPage.content.size, pageSize)
    }

    @Test
    fun `findAll - returns only tasks matching status filter`() {
        val taskNewSize = 5
        val taskInProgressionSize = 3

        val newTasks = (1..taskNewSize).map {
            val task = Task(
                title = "test-filter",
                description = "desc-filter",
                status = TaskStatus.NEW
            )
            taskRepository.save(task)
        }

        val inProgressionTasks = (1..taskInProgressionSize).map {
            val task = Task(
                title = "test-filter",
                description = "desc-filter",
                status = TaskStatus.IN_PROGRESS
            )
            taskRepository.save(task)
        }

        assertEquals(newTasks.size, taskNewSize)
        assertEquals(inProgressionTasks.size, taskInProgressionSize)

        val noCondition = taskSpecificationBuilder.build(TaskFilter())
        val allTasks = taskRepository.findAll(noCondition, PageRequest.of(0, 20))

        assertEquals(allTasks.totalElements, (taskNewSize + taskInProgressionSize).toLong())

        val condition = taskSpecificationBuilder.build(TaskFilter(status = TaskStatus.IN_PROGRESS))
        val filterByStatusTasks = taskRepository.findAll(condition, PageRequest.of(0, 20))

        assertEquals(filterByStatusTasks.totalElements, taskInProgressionSize.toLong())
    }

    private fun createTask(suffix: String): Task {
        val task = Task(
            title = "test-$suffix",
            description = "desc-$suffix",
        )

        return taskRepository.save(task)
    }

}