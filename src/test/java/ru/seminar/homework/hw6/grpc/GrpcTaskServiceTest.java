package ru.seminar.homework.hw6.grpc;

import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.seminar.homework.hw6.Hw6Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test",
        "grpc.server.port=9091",
        "grpc.client.taskService.address=in-process:test"
})
@SpringJUnitConfig(classes = {Hw6Application.class})
@Log4j2
class GrpcTaskServiceTest {

    @GrpcClient("taskService")
    private TaskServiceGrpc.TaskServiceBlockingStub taskServiceBlockingStub;

    @Test
    public void should_return_new_task() {
        Schema.GrpcTaskResponse resp = taskServiceBlockingStub.createTask(Schema.CreateTaskRequest.newBuilder().build());
        assertThat(resp).isNotNull()
                .hasFieldOrProperty("number")
                .hasFieldOrProperty("status")
                .hasFieldOrPropertyWithValue("status", "NEW");
    }
}