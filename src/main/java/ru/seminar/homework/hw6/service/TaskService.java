package ru.seminar.homework.hw6.service;

import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.dto.UpdateTaskDto;

import java.util.List;
import java.util.Map;

public interface TaskService {
    TaskDto createTask();

    TaskDto deleteTask(String number);

    TaskDto getNextWaitingTask();

    Map<String, List<String>> getTaskNumbersGroupedByStatuses();

    TaskDto updateTask(UpdateTaskDto dto);
}
