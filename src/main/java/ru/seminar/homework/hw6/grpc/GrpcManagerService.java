package ru.seminar.homework.hw6.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.seminar.homework.hw6.dto.TimeDto;
import ru.seminar.homework.hw6.service.StatisticsService;

@GrpcService
@RequiredArgsConstructor
public class GrpcManagerService extends ManagerServiceGrpc.ManagerServiceImplBase {
    private final StatisticsService statisticsService;
    private final GrpcExceptionHandler handler;

    @Override
    public void getAverageStatusProcessingTime(Schema.GetAverageStatusProcessingTimeRequest request,
                                               StreamObserver<Schema.GrpcTimeResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TimeDto time = statisticsService.getAverageStatusProcessingTime(request.getStatus());
            responseObserver.onNext(Schema.GrpcTimeResponse.newBuilder().setTime(time.getTime()).build());
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void getAverageTaskProcessingTime(Schema.Empty request,
                                             StreamObserver<Schema.GrpcTimeResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TimeDto time = statisticsService.getAverageTasksProcessingTime();
            responseObserver.onNext(Schema.GrpcTimeResponse.newBuilder().setTime(time.getTime()).build());
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void getTaskProcessingTime(Schema.GetTaskProcessingTimeRequest request,
                                      StreamObserver<Schema.GrpcTimeResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TimeDto time = statisticsService.getTaskProcessingTime(request.getNumber());
            responseObserver.onNext(Schema.GrpcTimeResponse.newBuilder().setTime(time.getTime()).build());
            responseObserver.onCompleted();
        }, responseObserver);
    }

    @Override
    public void getTaskStatusProcessingTime(Schema.GetTaskStatusProcessingTimeRequest request,
                                            StreamObserver<Schema.GrpcTimeResponse> responseObserver) {
        handler.decorateHandlingExceptions(() -> {
            TimeDto time = statisticsService.getTaskProcessingTimeInStatus(request.getNumber(), request.getStatus());
            responseObserver.onNext(Schema.GrpcTimeResponse.newBuilder().setTime(time.getTime()).build());
            responseObserver.onCompleted();
        }, responseObserver);
    }
}
