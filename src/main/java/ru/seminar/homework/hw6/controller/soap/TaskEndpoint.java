package ru.seminar.homework.hw6.controller.soap;

import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.seminar.homework.hw6.dto.*;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.mapper.TaskMapper;
import ru.seminar.homework.hw6.service.TaskService;

import java.util.List;
import java.util.Map;

@Endpoint
@RequiredArgsConstructor
public class TaskEndpoint {
    private static final String NAMESPACE_URI = "http://seminar.ru/homework/hw6/dto";

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addTaskRequest")
    @ResponsePayload
    public TaskResponse addTask() {
        TaskDto task = taskService.createTask();
        return taskMapper.toTaskResponse(task);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteTaskRequest")
    @ResponsePayload
    public TaskResponse deleteTask(@RequestPayload DeleteTaskRequest request) {
        TaskDto task = taskService.deleteTask(request.getNumber());
        return taskMapper.toTaskResponse(task);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNextWaitingTaskRequest")
    @ResponsePayload
    public TaskResponse getNextWaitingTask() {
        TaskDto task = taskService.getNextWaitingTask();
        return taskMapper.toTaskResponse(task);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTaskNumbersGroupedByStatusesRequest")
    @ResponsePayload
    public StatusesWithTaskNumbersResponse getTaskNumbersGroupedByStatuses() {
        Map<String, List<String>> result = taskService.getTaskNumbersGroupedByStatuses();
        List<MapEntry> list = result.entrySet().stream().map(e -> {
            var entry = new MapEntry();
            entry.setKey(e.getKey());
            entry.getValue().addAll(e.getValue());
            return entry;
        }).toList();

        var content = new ContentMap();
        content.getEntry().addAll(list);

        var dto = new StatusesWithTaskNumbersResponse();
        dto.setContent(content);
        return dto;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateTaskStatusRequest")
    @ResponsePayload
    public TaskResponse updateTaskStatus(@RequestPayload UpdateTaskStatusRequest request) {
        TaskDto task = taskService.updateTask(new UpdateTaskDto(request.getNumber(), TaskStatus.valueOf(request.getStatus())));
        return taskMapper.toTaskResponse(task);
    }
}
