package ru.seminar.homework.hw6.grpc;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import ru.seminar.homework.hw6.exception.ExistsException;
import ru.seminar.homework.hw6.exception.NotFoundException;
import ru.seminar.homework.hw6.exception.ServiceException;
import ru.seminar.homework.hw6.exception.ServiceValidationException;

import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.NOT_FOUND;

@Component
public class GrpcExceptionHandler {
    private static StatusRuntimeException handleException(String code, Status notFound, String message) {
        var key = ProtoUtils.keyForProto(Schema.ExceptionResponse.getDefaultInstance());
        var errorResponse = Schema.ExceptionResponse.newBuilder()
                .setCode(code)
                .setMessage(message)
                .build();
        Metadata metadata = new Metadata();
        metadata.put(key, errorResponse);
        return notFound.withDescription(message)
                .asRuntimeException(metadata);
    }

    public <RS> void decorateHandlingExceptions(Runnable consumer, StreamObserver<RS> responseObserver) {
        StatusRuntimeException exception;
        try {
            consumer.run();
            return;

        } catch (NotFoundException ex) {
            exception = handleException("404", NOT_FOUND, ex.getMessage());
        } catch (ServiceValidationException | NullPointerException | ExistsException ex) {
            exception = handleException("400", Status.INVALID_ARGUMENT, ex.getMessage());
        } catch (ServiceException ex) {
            exception = handleException("500", INTERNAL, "Service unavailable.");
        }
        responseObserver.onError(exception);
    }
}
