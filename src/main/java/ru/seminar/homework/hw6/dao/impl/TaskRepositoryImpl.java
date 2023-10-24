package ru.seminar.homework.hw6.dao.impl;

import org.springframework.stereotype.Repository;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.exception.DaoException;
import ru.seminar.homework.hw6.model.Task;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.seminar.homework.hw6.enums.TaskStatus.CANCEL;
import static ru.seminar.homework.hw6.enums.TaskStatus.CLOSE;

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

    @Override
    public Duration getAverageTasksProcessingTime() {
        List<Duration> times = data.values().stream()
                .filter(t -> t.getStatus() != CANCEL && t.getStatus() != CLOSE)
                .map(t -> t.getTimes().values().stream()
                        .reduce(Duration::plus)
                        .orElse(Duration.ZERO))
                .toList();
        if (times.isEmpty())
            return Duration.ZERO;
        return times.stream()
                .reduce(Duration::plus)
                .orElse(Duration.ZERO)
                .dividedBy(times.size());
    }

    @Override
    public Duration getAverageStatusProcessingTime(TaskStatus status) {
        List<Duration> times = data.values().stream()
                .filter(t -> t.getStatus() != CANCEL && t.getStatus() != CLOSE)
                .map(t -> t.getTimes().get(status))
                .toList();

        if (times.isEmpty())
            return Duration.ZERO;
        return times.stream()
                .reduce(Duration::plus)
                .orElse(Duration.ZERO)
                .dividedBy(times.size());
    }

    private Task clone(Task task) {
        if (Objects.isNull(task)) return null;

        Map<TaskStatus, Duration> newMap = new HashMap<>();
        for (var status : TaskStatus.values)
            newMap.put(status, task.getTimes().getOrDefault(status, Duration.ZERO));

        return new Task(
                task.getNumber(),
                task.getStatus(),
                task.getLastUpdatedAt(),
                newMap);
    }
}
