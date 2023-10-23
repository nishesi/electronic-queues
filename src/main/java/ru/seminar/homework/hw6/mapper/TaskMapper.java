package ru.seminar.homework.hw6.mapper;

import org.mapstruct.Mapper;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.model.Task;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TaskMapper {
    TaskDto from(Task task);
}
