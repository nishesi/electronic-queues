package ru.seminar.homework.hw6.dao;

import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.model.Task;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findByNumber(String number);

    void deleteByNumber(String number);
    List<Task> findAllByStatus(TaskStatus status);

    Duration getAverageTasksProcessingTime();

    Duration getAverageStatusProcessingTime(TaskStatus status);
}
