package ru.seminar.homework.hw6.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.mapper.TaskMapper;
import ru.seminar.homework.hw6.model.Task;
import ru.seminar.homework.hw6.service.IdGenerator;
import ru.seminar.homework.hw6.service.TaskService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.seminar.homework.hw6.enums.TaskStatus.*;

class TaskServiceImplTest {
    private TaskService taskService;
    private TaskRepository taskRepository;
    private IdGenerator idGenerator;
    private TaskMapper taskMapper;
    private Clock clock;

    @BeforeEach
    public void init() {
        taskRepository = mock(TaskRepository.class);
        idGenerator = new IdGenerator();
        taskMapper = mock(TaskMapper.class);
        clock = Clock.systemUTC();
        taskService = new TaskServiceImpl(taskRepository, idGenerator, taskMapper, clock);
    }

    @Nested
    public class test_method_cancelLongTasks {
        @Test
        public void should_cancel_all_tasks() throws InterruptedException {
            taskService = new TaskServiceImpl(taskRepository, idGenerator, taskMapper, Clock.offset(clock, Duration.ofMinutes(31)));
            var shouldBeCanceled = List.of(
                    new Task("21", WAITING, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(10),
                            WAITING, Duration.ofSeconds(1810),
                            PROCESSED, Duration.ZERO,
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    ))),
                    new Task("22", WAITING, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(10),
                            WAITING, Duration.ZERO,
                            PROCESSED, Duration.ZERO,
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    ))),
                    new Task("31", PROCESSED, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(10),
                            WAITING, Duration.ofSeconds(10),
                            PROCESSED, Duration.ofSeconds(1810),
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    ))),
                    new Task("31", PROCESSED, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(10),
                            WAITING, Duration.ofSeconds(1810),
                            PROCESSED, Duration.ZERO,
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    )))
            );
            when(taskRepository.findAll()).thenReturn(shouldBeCanceled);

            doAnswer(invocation -> {
                List<Task> list = invocation.getArgument(0);
                assertThat(list)
                        .containsOnly(shouldBeCanceled.toArray(Task[]::new));
                return null;
            }).when(taskRepository).saveAll(any());

            taskService.cancelLongTasks();
        }

        @Test
        public void should_not_cancel_tasks() {
            taskService = new TaskServiceImpl(taskRepository, idGenerator, taskMapper, Clock.tickMinutes(ZoneId.systemDefault()));
            var shouldNotBeCanceled = List.of(
                    new Task("11", NEW, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(1810), // > 30 min
                            WAITING, Duration.ZERO,
                            PROCESSED, Duration.ZERO,
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    ))),
                    new Task("12", NEW, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ZERO,
                            WAITING, Duration.ZERO,
                            PROCESSED, Duration.ZERO,
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    ))),
                    new Task("31", CLOSE, LocalDateTime.now(clock), new HashMap<>(Map.of(
                            NEW, Duration.ofSeconds(10),
                            WAITING, Duration.ofSeconds(1810),
                            PROCESSED, Duration.ofSeconds(10),
                            CANCEL, Duration.ZERO,
                            CLOSE, Duration.ZERO
                    )))
            );

            when(taskRepository.findAll()).thenReturn(shouldNotBeCanceled);
            doAnswer(invocation -> {
                List<Task> list = invocation.getArgument(0);
                assertThat(list)
                        .isEmpty();
                return null;
            }).when(taskRepository).saveAll(any());

            taskService.cancelLongTasks();
        }
    }
}