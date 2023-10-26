package ru.seminar.homework.hw6.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.seminar.homework.hw6.TaskResponse;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.model.Task;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TaskMapper {
    TaskDto toTaskDto(Task task);

    @Mapping(target = "status", expression = "java(taskDto.getStatus().toString())")
    TaskResponse toTaskResponse(TaskDto taskDto);
}
