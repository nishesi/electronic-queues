package ru.seminar.homework.hw6.service;

import ru.seminar.homework.hw6.dto.TimeDto;

public interface StatisticsService {
    TimeDto getAverageTasksProcessingTime();

    TimeDto getTaskProcessingTime(String number);

    TimeDto getTaskProcessingTimeInStatus(String number, String status);

    TimeDto getAverageStatusProcessingTime(String status);
}
