package ru.seminar.homework.hw6.controller.soap;

import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.seminar.homework.hw6.GetAverageStatusProcessingTimeRequest;
import ru.seminar.homework.hw6.GetTaskProcessingTimeRequest;
import ru.seminar.homework.hw6.GetTaskStatusProcessingTimeRequest;
import ru.seminar.homework.hw6.TimeResponse;
import ru.seminar.homework.hw6.dto.TimeDto;
import ru.seminar.homework.hw6.service.StatisticsService;

@Endpoint
@RequiredArgsConstructor
public class ManagerEndpoint {
    private static final String NAMESPACE_URI = "http://seminar.ru/homework/hw6";

    private final StatisticsService statisticsService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAverageStatusProcessingTimeRequest")
    @ResponsePayload
    public TimeResponse getAverageStatusProcessingTime(@RequestPayload GetAverageStatusProcessingTimeRequest request) {
        TimeDto time = statisticsService.getAverageStatusProcessingTime(request.getStatus());
        var dto = new TimeResponse();
        dto.setTime(time.getTime());
        return dto;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAverageTaskProcessingTimeRequest")
    @ResponsePayload
    public TimeResponse getAverageTaskProcessingTime() {
        TimeDto time = statisticsService.getAverageTasksProcessingTime();
        var dto = new TimeResponse();
        dto.setTime(time.getTime());
        return dto;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTaskProcessingTimeRequest")
    @ResponsePayload
    public TimeResponse getTaskProcessingTime(@RequestPayload GetTaskProcessingTimeRequest request) {
        TimeDto time = statisticsService.getTaskProcessingTime(request.getNumber());
        var dto = new TimeResponse();
        dto.setTime(time.getTime());
        return dto;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTaskStatusProcessingTimeRequest")
    @ResponsePayload
    public TimeResponse getTaskStatusProcessingTime(@RequestPayload GetTaskStatusProcessingTimeRequest request) {
        TimeDto time = statisticsService.getTaskProcessingTimeInStatus(request.getNumber(), request.getStatus());
        var dto = new TimeResponse();
        dto.setTime(time.getTime());
        return dto;
    }
}
