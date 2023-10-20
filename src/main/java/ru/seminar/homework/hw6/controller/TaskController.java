package ru.seminar.homework.hw6.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.seminar.homework.hw6.dto.StatusesWithTasksDto;
import ru.seminar.homework.hw6.dto.TaskDto;

@RestController
public class TaskController implements TaskApi  {
    @Override
    public ResponseEntity<TaskDto> addTask() {
        return TaskApi.super.addTask();
    }

    @Override
    public ResponseEntity<TaskDto> deleteTask(String number) {
        return TaskApi.super.deleteTask(number);
    }

    @Override
    public ResponseEntity<TaskDto> getNextWaitingTask() {
        return TaskApi.super.getNextWaitingTask();
    }

    @Override
    public ResponseEntity<StatusesWithTasksDto> getStatusesAndTasksMap() {
        return TaskApi.super.getStatusesAndTasksMap();
    }

    @Override
    public ResponseEntity<TaskDto> updateTaskStatus(String number, String status) {
        return TaskApi.super.updateTaskStatus(number, status);
    }
}
