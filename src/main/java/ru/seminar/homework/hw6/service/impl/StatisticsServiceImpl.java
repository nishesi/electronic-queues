package ru.seminar.homework.hw6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.dto.TimeDto;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.exception.NotFoundException;
import ru.seminar.homework.hw6.exception.ServiceValidationException;
import ru.seminar.homework.hw6.model.Task;
import ru.seminar.homework.hw6.service.StatisticsService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static ru.seminar.homework.hw6.enums.TaskStatus.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final TaskRepository taskRepository;

    private static TaskStatus parseStatus(String status) {
        try {
            return TaskStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new ServiceValidationException("Status = " + status + " not exists");
        }
    }

    @Override
    public TimeDto getAverageTasksProcessingTime() {
        Duration duration = taskRepository.getAverageTasksProcessingTime();
        return new TimeDto().time(duration.getSeconds());
    }

    @Override
    public TimeDto getTaskProcessingTime(String number) {
        Task task = taskRepository.findByNumber(number)
                .orElseThrow(() -> new NotFoundException("Task with number = " + number + " not found"));

        Map<TaskStatus, Duration> times = task.getTimes();
        Duration plus = times.get(NEW).plus(times.get(WAITING)).plus(times.get(PROCESSED));
        if (task.getStatus() != CANCEL && task.getStatus() != CLOSE)
            plus = plus.plus(Duration.between(task.getLastUpdatedAt(), LocalDateTime.now()));

        return new TimeDto().time(plus.getSeconds());
    }

    @Override
    public TimeDto getTaskProcessingTimeInStatus(String number, String statusStr) {
        Task task = taskRepository.findByNumber(number)
                .orElseThrow(() -> new NotFoundException("Task with number = " + number + " not found"));


        TaskStatus status = parseStatus(statusStr);
        if (status == CANCEL || status == CLOSE)
            throw new ServiceValidationException("Time for status = " + statusStr + " not defined.");
        Duration duration = task.getTimes().get(status);

        return new TimeDto().time(duration.getSeconds());
    }

    @Override
    public TimeDto getAverageStatusProcessingTime(String statusStr) {
        TaskStatus status = parseStatus(statusStr);
        if (status == CANCEL || status == CLOSE)
            throw new ServiceValidationException("Time for status = " + statusStr + " not defined.");
        Duration duration = taskRepository.getAverageStatusProcessingTime(status);
        return new TimeDto().time(duration.getSeconds());
    }
}
