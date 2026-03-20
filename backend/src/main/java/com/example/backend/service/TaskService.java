package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Task.Priority;
import com.example.backend.entity.Task.TaskStatus;
import com.example.backend.models.request.CreateTaskRequest;
import com.example.backend.models.request.UpdateTaskRequest;
import com.example.backend.models.response.TaskResponse;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTaskById(Long id);

    List<TaskResponse> getAllTasks(TaskStatus status, Priority priority);

    TaskResponse updateTask(Long id, UpdateTaskRequest request);

    void deleteTask(Long id);
}
