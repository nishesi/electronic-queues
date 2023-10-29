package ru.seminar.homework.hw6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.dto.TaskDto;
import ru.seminar.homework.hw6.dto.UpdateTaskDto;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.exception.NotFoundException;
import ru.seminar.homework.hw6.exception.ServiceValidationException;
import ru.seminar.homework.hw6.mapper.TaskMapper;
import ru.seminar.homework.hw6.model.Task;
import ru.seminar.homework.hw6.service.IdGenerator;
import ru.seminar.homework.hw6.service.TaskService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ru.seminar.homework.hw6.enums.TaskStatus.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final IdGenerator idGenerator;
    private final TaskMapper taskMapper;
    private final Clock clock;
    private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    private void updateStatusTime(Task task) {
        Duration duration = task.getTimes().get(task.getStatus());
        duration = duration.plus(Duration.between(task.getLastUpdatedAt(), LocalDateTime.now(clock)));
        task.getTimes().put(task.getStatus(), duration);
    }

    @Override
    public TaskDto createTask() {
        Task task = new Task(idGenerator.generate(), NEW, LocalDateTime.now(clock), new HashMap<>());
        task = taskRepository.save(task);
        return taskMapper.toTaskDto(task);
    }

    @Override
    public TaskDto deleteTask(String number) {
        Task task = taskRepository.findByNumber(number)
                .orElseThrow(() -> new NotFoundException("Task with number = " + number + " not found."));

        taskRepository.deleteByNumber(task.getNumber());

        return taskMapper.toTaskDto(task);
    }

    @Override
    public TaskDto getNextWaitingTask() {
        Task task = taskQueue.peek();
        if (task == null)
            throw new NotFoundException("No waiting tasks");
        return taskMapper.toTaskDto(task);
    }

    @Override
    public Map<String, List<String>> getTaskNumbersGroupedByStatuses() {
        Map<String, List<String>> map = new HashMap<>();
        for (var status : new TaskStatus[]{NEW, WAITING, PROCESSED}) {
            List<String> tasks = taskRepository.findAllByStatus(status).stream()
                    .map(Task::getNumber)
                    .toList();

            map.put(status.name(), tasks);
        }
        return map;
    }

    @Override
    public TaskDto updateTask(UpdateTaskDto dto) {
        Task task = taskRepository.findByNumber(dto.getNumber())
                .orElseThrow(() -> new NotFoundException("Task with number = " + dto.getNumber() + " not found."));

        // status updating constraints
        List<TaskStatus> statuses = TaskStatus.values;
        if (Math.abs(statuses.indexOf(dto.getStatus()) - statuses.indexOf(task.getStatus())) != 1
                && dto.getStatus() != CANCEL)
            throw new ServiceValidationException("Status can not be changed with skips");

        // check queue
        if (dto.getStatus() == WAITING) taskQueue.add(task);
        else if (task.getStatus() == WAITING) taskQueue.remove(task);

        // calculate and save stay time in status
        updateStatusTime(task);

        task.setStatus(dto.getStatus());
        task = taskRepository.save(task);

        return taskMapper.toTaskDto(task);
    }

    @Override
    public void cancelLongTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<Task> updatedTasks = new ArrayList<>();
        for (Task task : tasks) {
            TaskStatus status = task.getStatus();

            if (status != WAITING && status != PROCESSED) continue;

            LocalDateTime time = task.getLastUpdatedAt().plus(Duration.ofMinutes(30));
            LocalDateTime now = LocalDateTime.now(clock);
            if (time.isBefore(now)) {
                updateStatusTime(task);
                task.setStatus(CANCEL);
                task.setLastUpdatedAt(now);

                if (status == WAITING)
                    taskQueue.remove(task);
                updatedTasks.add(task);
            }
        }
        taskRepository.saveAll(updatedTasks);
    }
}
