package ru.seminar.homework.hw6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.seminar.homework.hw6.dto.StatusesWithTaskNumbersDto;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.dto.UpdateTaskDto;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.service.TaskService;
import ru.seminar.homework.hw6.controller.api.TaskApi;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi  {
    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskDto> addTask() {
        TaskDto task = taskService.createTask();
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @Override
    public ResponseEntity<TaskDto> deleteTask(String number) {
        TaskDto task = taskService.deleteTask(number);
        return ResponseEntity.accepted().body(task);
    }

    @Override
    public ResponseEntity<TaskDto> getNextWaitingTask() {
        TaskDto task = taskService.getNextWaitingTask();
        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<StatusesWithTaskNumbersDto> getTaskNumbersGroupedByStatuses() {
        Map<String, List<String>> result = taskService.getTaskNumbersGroupedByStatuses();
        return ResponseEntity.ok(new StatusesWithTaskNumbersDto().content(result));
    }

    @Override
    public ResponseEntity<TaskDto> updateTaskStatus(String number, TaskStatus status) {
        TaskDto task = taskService.updateTask(new UpdateTaskDto(number, status));
        return ResponseEntity.accepted().body(task);
    }
}
