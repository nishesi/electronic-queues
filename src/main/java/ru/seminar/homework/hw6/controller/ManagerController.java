package ru.seminar.homework.hw6.controller;

import org.springframework.http.ResponseEntity;
import ru.seminar.homework.hw6.dto.TimeDto;

public class ManagerController implements ManagerApi {
    @Override
    public ResponseEntity<TimeDto> getAverageStatusProcessingTime(String status) {
        return ManagerApi.super.getAverageStatusProcessingTime(status);
    }

    @Override
    public ResponseEntity<TimeDto> getAverageTaskProcessingTime() {
        return ManagerApi.super.getAverageTaskProcessingTime();
    }

    @Override
    public ResponseEntity<TimeDto> getTaskProcessingTime(String number) {
        return ManagerApi.super.getTaskProcessingTime(number);
    }

    @Override
    public ResponseEntity<TimeDto> getTaskStatusProcessingTime(String number, String status) {
        return ManagerApi.super.getTaskStatusProcessingTime(number, status);
    }
}
