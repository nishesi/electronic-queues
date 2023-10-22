package ru.seminar.homework.hw6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.dto.TimeDto;
import ru.seminar.homework.hw6.service.StatisticsService;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final TaskRepository taskRepository;

    @Override
    public TimeDto getAverageTasksProcessingTime() {
        return null;
    }

    @Override
    public TimeDto getTaskProcessingTime(String number) {
        return null;
    }

    @Override
    public TimeDto getTaskProcessingTimeInStatus(String number, String status) {
        return null;
    }

    @Override
    public TimeDto getAverageStatusProcessingTime(String status) {
        return null;
    }
}
