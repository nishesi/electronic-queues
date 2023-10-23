package ru.seminar.homework.hw6.dao.impl;

import org.springframework.stereotype.Repository;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.exception.DaoException;
import ru.seminar.homework.hw6.model.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private final Map<String, Task> data = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        Objects.requireNonNull(task);
        if (Objects.isNull(task.getNumber()))
            throw new DaoException("null id", 0);

        data.put(task.getNumber(), clone(task));
        return task;
    }

    @Override
    public Optional<Task> findByNumber(String number) {
        if (Objects.isNull(number))
            throw new DaoException("null id", 0);
        Task task = clone(data.get(number));
        return Optional.ofNullable(task);
    }

    @Override
    public void deleteByNumber(String number) {
        if (Objects.isNull(number))
            throw new DaoException("null id", 0);
        data.remove(number);
    }

    @Override
    public List<Task> findAllByStatus(TaskStatus status) {
        if (Objects.isNull(status))
            throw new DaoException("null argument", 3);
        return data.values().stream()
                .filter(task -> task.getStatus() == status)
                .map(this::clone)
                .toList();
    }

    private Task clone(Task task) {
        if (Objects.isNull(task)) return null;
        return new Task(
                task.getNumber(),
                task.getStatus(),
                task.getLastChangedAt(),
                new HashMap<>(task.getTimes()));
    }
}
