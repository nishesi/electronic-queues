package ru.seminar.homework.hw6.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.seminar.homework.hw6.dao.TaskRepository;
import ru.seminar.homework.hw6.enums.TaskStatus;
import ru.seminar.homework.hw6.exception.DaoException;
import ru.seminar.homework.hw6.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.seminar.homework.hw6.enums.TaskStatus.*;

class TaskRepositoryImplTest {
    private TaskRepository taskRepository;

    @BeforeEach
    public void init() {
        taskRepository = new TaskRepositoryImpl();
    }

    @Nested
    public class test_method_save {
        @Test
        public void should_return_equal_task() {
            String id = "123";
            TaskStatus status = CLOSE;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(NEW, Duration.ofSeconds(100));

            Task task = new Task(id, status, lastUpdated, times);

            Task savedTask = taskRepository.save(task);

            assertThat(savedTask)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("number", id)
                    .hasFieldOrPropertyWithValue("status", status)
                    .hasFieldOrPropertyWithValue("lastUpdatedAt", lastUpdated)
                    .hasFieldOrPropertyWithValue("times", times);
        }

        @Test
        public void should_return_empty_optional_on_unknown_number() {
            assertThat(taskRepository.findByNumber("123"))
                    .isNotNull()
                    .isNotPresent();
        }

        @Test
        public void should_not_change_parameter_task() {
            String id = "123";
            TaskStatus status = CLOSE;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(NEW, Duration.ofSeconds(100));

            Task task = new Task(id, status, lastUpdated, times);

            taskRepository.save(task);

            assertThat(task)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("number", id)
                    .hasFieldOrPropertyWithValue("status", status)
                    .hasFieldOrPropertyWithValue("lastUpdatedAt", lastUpdated)
                    .hasFieldOrPropertyWithValue("times", times);
        }

        @Test
        public void should_save_task() {
            String id = "123";
            TaskStatus status = CLOSE;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(
                    NEW, Duration.ofSeconds(100),
                    WAITING, Duration.ZERO,
                    PROCESSED, Duration.ZERO,
                    CLOSE, Duration.ZERO,
                    CANCEL, Duration.ZERO
            );

            Task task = new Task(id, status, lastUpdated, times);

            taskRepository.save(task);
            Optional<Task> foundTask = taskRepository.findByNumber(id);

            assertThat(foundTask)
                    .isNotNull()
                    .isPresent()
                    .get()
                    .hasFieldOrPropertyWithValue("number", id)
                    .hasFieldOrPropertyWithValue("status", status)
                    .hasFieldOrPropertyWithValue("lastUpdatedAt", lastUpdated)
                    .hasFieldOrPropertyWithValue("times", times);
        }

        @Test
        public void should_throw_exception_on_null_id() {
            String id = null;
            TaskStatus status = CLOSE;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(NEW, Duration.ofSeconds(100));

            Task task = new Task(id, status, lastUpdated, times);

            assertThatThrownBy(() -> taskRepository.save(task))
                    .isInstanceOf(DaoException.class)
                    .hasFieldOrPropertyWithValue("code", 0);
        }
    }

    @Nested
    public class test_method_findByNumber {
        @Test
        public void should_throw_exception_on_null_id() {
            assertThatThrownBy(() -> taskRepository.findByNumber(null))
                    .isInstanceOf(DaoException.class)
                    .hasFieldOrPropertyWithValue("code", 0);
        }

        @Test
        public void should_return_empty_optional() {
            Optional<Task> task = taskRepository.findByNumber("unknownid");

            assertThat(task)
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        public void should_return_correct_task() {
            String id = "1234567";
            TaskStatus status = CANCEL;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(NEW, Duration.ofSeconds(150),
                    WAITING, Duration.ofSeconds(20),
                    PROCESSED, Duration.ofSeconds(1859),
                    CANCEL, Duration.ZERO,
                    CLOSE, Duration.ZERO);

            Task task = new Task(id, status, lastUpdated, times);
            var list = List.of(
                    new Task("123", NEW, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("1234", WAITING, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100), WAITING, Duration.ofSeconds(200))),
                    new Task("12345", PROCESSED, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("123456", CLOSE, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    task
            );
            taskRepository.saveAll(list);

            Optional<Task> foundTask = taskRepository.findByNumber("1234567");

            assertThat(foundTask)
                    .isNotNull()
                    .isPresent()
                    .get()
                    .hasFieldOrPropertyWithValue("number", id)
                    .hasFieldOrPropertyWithValue("status", status)
                    .hasFieldOrPropertyWithValue("lastUpdatedAt", lastUpdated)
                    .hasFieldOrPropertyWithValue("times", times);
        }
    }

    @Nested
    public class test_method_deleteByNumber {
        @Test
        public void should_throw_exception_on_null_id() {
            assertThatThrownBy(() -> taskRepository.deleteByNumber(null))
                    .isInstanceOf(DaoException.class)
                    .hasFieldOrPropertyWithValue("code", 0);
        }

        @Test
        public void should_delete_task() {
            String id = "1234567";
            TaskStatus status = CANCEL;
            var lastUpdated = LocalDateTime.now();
            var times = Map.of(NEW, Duration.ofSeconds(150), WAITING, Duration.ofSeconds(20), PROCESSED, Duration.ofSeconds(1859));

            Task task = new Task(id, status, lastUpdated, times);
            taskRepository.save(task);
            var list = List.of(
                    new Task("123", NEW, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("1234", WAITING, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100), WAITING, Duration.ofSeconds(200))),
                    new Task("12345", PROCESSED, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("123456", CLOSE, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100)))
            );
            taskRepository.saveAll(list);

            Optional<Task> found = taskRepository.findByNumber("1234567");

            assertThat(found)
                    .isNotNull()
                    .isPresent();

            taskRepository.deleteByNumber("1234567");

            Optional<Task> notFound = taskRepository.findByNumber("1234567");

            assertThat(notFound)
                    .isNotNull()
                    .isEmpty();

            for (Task task1 : list) {
                Optional<Task> task2 = taskRepository.findByNumber(task1.getNumber());

                assertThat(task2)
                        .isNotNull()
                        .isPresent();
            }
        }
    }

    @Nested
    public class test_method_findAllByStatus {
        @Test
        public void should_throw_exception_on_null_status() {
            assertThatThrownBy(() -> taskRepository.findAllByStatus(null))
                    .isInstanceOf(DaoException.class)
                    .hasFieldOrPropertyWithValue("code", 3);
        }

        @Test
        public void should_return_empty_list() {
            for (TaskStatus status : values()) {
                List<Task> tasks = taskRepository.findAllByStatus(status);

                assertThat(tasks)
                        .isNotNull()
                        .isEmpty();
            }
        }

        @Test
        public void should_return_correct_list() {
            Task t1 = new Task("1234", WAITING, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100), WAITING, Duration.ofSeconds(200)));
            Task t2 = new Task("B", WAITING, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100)));
            var list = List.of(
                    t1,
                    t2,
                    new Task("123", NEW, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("12345", PROCESSED, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("123456", CLOSE, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("A", CLOSE, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100))),
                    new Task("C", CLOSE, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(100)))
            );

            taskRepository.saveAll(list);

            List<Task> tasks = taskRepository.findAllByStatus(WAITING);

            assertThat(tasks)
                    .isNotNull()
                    .isNotEmpty()
                    .containsOnly(t1, t2);
        }
    }

    @Nested
    public class test_method_getAverageTasksProcessingTime {

        @Test
        public void should_return_zero_on_empty_base() {
            Duration duration = taskRepository.getAverageTasksProcessingTime();

            assertThat(duration)
                    .isNotNull()
                    .isZero();
        }
        @Test
        public void should_return_correct_value() {
            var map1 = Map.of(
                    NEW, Duration.ofSeconds(100),
                    WAITING, Duration.ofSeconds(200),
                    PROCESSED, Duration.ofSeconds(300),
                    CANCEL, Duration.ofSeconds(400),
                    CLOSE, Duration.ofSeconds(500)
            );
            var map2 = Map.of(
                    NEW, Duration.ofSeconds(1000),
                    WAITING, Duration.ofSeconds(2000),
                    PROCESSED, Duration.ofSeconds(3000),
                    CANCEL, Duration.ofSeconds(4000),
                    CLOSE, Duration.ofSeconds(5000)
            );
            var map3 = Map.of(
                    NEW, Duration.ofSeconds(150),
                    WAITING, Duration.ofSeconds(20),
                    PROCESSED, Duration.ofSeconds(1330)
            );
            var list = List.of(
                    new Task("123", NEW, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(5000000))),
                    new Task("12345", PROCESSED, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(1), WAITING, Duration.ofSeconds(2))),
                    new Task("A", CLOSE, LocalDateTime.now(), map1),
                    new Task("C", CLOSE, LocalDateTime.now(), map2),
                    new Task("B", CANCEL, LocalDateTime.now(), map3)
            );
            taskRepository.saveAll(list);

            Duration duration = taskRepository.getAverageTasksProcessingTime();

            assertThat(duration)
                    .isNotNull()
                    .hasSeconds(6000);
        }
    }
    @Nested
    public class test_method_getAverageStatusProcessingTime {
        @Test
        public void should_return_zero_on_empty_base() {
            for (var status : TaskStatus.values()) {
                Duration duration = taskRepository.getAverageStatusProcessingTime(status);

                assertThat(duration)
                        .isNotNull()
                        .isZero();
            }
        }
        @Test
        public void should_return_correct_value() {
            var map1 = Map.of(
                    NEW, Duration.ofSeconds(100),
                    WAITING, Duration.ofSeconds(200),
                    PROCESSED, Duration.ofSeconds(300),
                    CANCEL, Duration.ofSeconds(400),
                    CLOSE, Duration.ofSeconds(500)
            );
            var map2 = Map.of(
                    NEW, Duration.ofSeconds(950),
                    WAITING, Duration.ofSeconds(2000),
                    PROCESSED, Duration.ofSeconds(3000),
                    CANCEL, Duration.ofSeconds(4000),
                    CLOSE, Duration.ofSeconds(5000)
            );
            var map3 = Map.of(
                    NEW, Duration.ofSeconds(150),
                    WAITING, Duration.ofSeconds(20),
                    PROCESSED, Duration.ofSeconds(1200)
            );
            var list = List.of(
                    new Task("123", NEW, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(5000000))),
                    new Task("12345", PROCESSED, LocalDateTime.now(), Map.of(NEW, Duration.ofSeconds(1), WAITING, Duration.ofSeconds(2))),
                    new Task("A", CLOSE, LocalDateTime.now(), map1),
                    new Task("C", CLOSE, LocalDateTime.now(), map2),
                    new Task("B", CANCEL, LocalDateTime.now(), map3)
            );
            taskRepository.saveAll(list);

            Duration duration = taskRepository.getAverageStatusProcessingTime(NEW);
            assertThat(duration)
                    .isNotNull()
                    .hasSeconds(400);

            duration = taskRepository.getAverageStatusProcessingTime(WAITING);
            assertThat(duration)
                    .isNotNull()
                    .hasSeconds(740);

            duration = taskRepository.getAverageStatusProcessingTime(PROCESSED);
            assertThat(duration)
                    .isNotNull()
                    .hasSeconds(1500);
        }
    }
}