package com.example.data.repository

import com.example.data.db.TaskDao
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
  val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
  val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()

  suspend fun insertTask(task: TaskEntity): Long {
    return taskDao.insertTask(task)
  }

  suspend fun insertTasks(tasks: List<TaskEntity>) {
    taskDao.insertTasks(tasks)
  }

  suspend fun updateTask(task: TaskEntity) {
    taskDao.updateTask(task)
  }

  suspend fun setTaskCompleted(id: Long, completed: Boolean) {
    taskDao.updateCompletionStatus(id, completed)
  }

  suspend fun deleteTask(task: TaskEntity) {
    taskDao.deleteTask(task)
  }

  suspend fun deleteById(id: Long) {
    taskDao.deleteById(id)
  }

  suspend fun clearAll() {
    taskDao.clearAll()
  }
}
