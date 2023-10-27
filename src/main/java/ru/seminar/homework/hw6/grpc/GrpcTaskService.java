package ru.seminar.homework.hw6.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.mapper.TaskMapper;
import ru.seminar.homework.hw6.service.TaskService;

import java.util.List;
import java.util.Map;

@GrpcService
@RequiredArgsConstructor
public class GrpcTaskService extends TaskServiceGrpc.TaskServiceImplBase {
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final GrpcExceptionHandler handler;

    @Override
    public void createTask(Schema.CreateTaskRequest request,
                           StreamObserver<Schema.GrpcTaskResponse> responseObserver) {
        TaskDto task = taskService.createTask();
        responseObserver.onNext(taskMapper.toGrpcTaskResponse(task));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTask(Schema.DeleteTaskRequest request,
                           StreamObserver<Schema.GrpcTaskResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TaskDto task = taskService.deleteTask(request.getNumber());
            responseObserver.onNext(taskMapper.toGrpcTaskResponse(task));
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void getNextWaitingTask(Schema.GetNextWaitingTaskRequest request,
                                   StreamObserver<Schema.GrpcTaskResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TaskDto task = taskService.getNextWaitingTask();
            responseObserver.onNext(taskMapper.toGrpcTaskResponse(task));
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void getTaskNumbersGroupedByStatuses(Schema.GetTaskNumbersGroupedByStatusesRequest request,
                                                StreamObserver<Schema.TaskNumbersGroupedByStatuses> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            Map<String, List<String>> result = taskService.getTaskNumbersGroupedByStatuses();
            List<Schema.Entry> list = result.entrySet().stream()
                    .map(e -> Schema.Entry.newBuilder()
                            .setStatus(e.getKey())
                            .addAllNumber(e.getValue())
                            .build())
                    .toList();
            responseObserver.onNext(
                    Schema.TaskNumbersGroupedByStatuses.newBuilder()
                            .addAllEntries(list)
                            .build());
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void updateTask(Schema.UpdateTaskRequest request,
                           StreamObserver<Schema.GrpcTaskResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TaskDto task = taskService.updateTask(taskMapper.toUpdateTaskDto(request));
            responseObserver.onNext(taskMapper.toGrpcTaskResponse(task));
            responseObserver.onCompleted();
        }, responseObserver);
    }
}
