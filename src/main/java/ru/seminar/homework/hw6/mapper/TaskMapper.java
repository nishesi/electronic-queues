package ru.seminar.homework.hw6.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.dto.TaskResponse;
import ru.seminar.homework.hw6.dto.UpdateTaskDto;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.grpc.Schema;
import ru.seminar.homework.hw6.model.Task;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = TaskStatus.class)
public interface TaskMapper {
    TaskDto toTaskDto(Task task);

    @Mapping(target = "status", expression = "java(taskDto.getStatus().toString())")
    TaskResponse toTaskResponse(TaskDto taskDto);

    @Mapping(target = "status", expression = "java(taskDto.getStatus().toString())")
    Schema.GrpcTaskResponse toGrpcTaskResponse(TaskDto taskDto);

    @Mapping(target = "status", expression = "java(TaskStatus.valueOf(request.getStatus()))")
    UpdateTaskDto toUpdateTaskDto(Schema.UpdateTaskRequest request);
}
