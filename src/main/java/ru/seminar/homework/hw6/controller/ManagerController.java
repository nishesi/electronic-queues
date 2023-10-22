package ru.seminar.homework.hw6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.seminar.homework.hw6.dto.TimeDto;
import ru.seminar.homework.hw6.service.StatisticsService;

@RestController
@RequiredArgsConstructor
public class ManagerController implements ManagerApi {
    private final StatisticsService statisticsService;
    @Override
    public ResponseEntity<TimeDto> getAverageStatusProcessingTime(String status) {
        TimeDto time = statisticsService.getAverageStatusProcessingTime(status);
        return ResponseEntity.ok(time);
    }

    @Override
    public ResponseEntity<TimeDto> getAverageTaskProcessingTime() {
        TimeDto time = statisticsService.getAverageTasksProcessingTime();
        return ResponseEntity.ok(time);
    }

    @Override
    public ResponseEntity<TimeDto> getTaskProcessingTime(String number) {
        TimeDto time = statisticsService.getTaskProcessingTime(number);
        return ResponseEntity.ok(time);
    }

    @Override
    public ResponseEntity<TimeDto> getTaskStatusProcessingTime(String number, String status) {
        TimeDto time = statisticsService.getTaskProcessingTimeInStatus(number, status);
        return ResponseEntity.ok(time);
    }
}
