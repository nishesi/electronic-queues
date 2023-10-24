package ru.seminar.homework.hw6.dao;

import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.model.Task;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    void saveAll(List<Task> tasks);

    Optional<Task> findByNumber(String number);

    List<Task> findAll();

    List<Task> findAllByStatus(TaskStatus status);

    void deleteByNumber(String number);

    Duration getAverageTasksProcessingTime();

    Duration getAverageStatusProcessingTime(TaskStatus status);
}
