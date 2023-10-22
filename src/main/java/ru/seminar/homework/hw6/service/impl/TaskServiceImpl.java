package ru.seminar.homework.hw6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.dto.UpdateTaskDto;
import ru.seminar.homework.hw6.service.TaskService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public TaskDto createTask() {
        return null;
    }

    @Override
    public TaskDto deleteTask(String number) {
        return null;
    }

    @Override
    public TaskDto getNextWaitingTask() {
        return null;
    }

    @Override
    public Map<String, List<String>> getTaskNumbersGroupedByStatuses() {
        return null;
    }

    @Override
    public TaskDto updateTask(UpdateTaskDto dto) {
        return null;
    }
}
