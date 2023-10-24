package ru.seminar.homework.hw6.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.seminar.homework.hw6.service.TaskService;

@Component
@Log4j2
@RequiredArgsConstructor
public class MyTaskScheduler {
    private final TaskService taskService;

    @Scheduled(cron = "0 * * * * *")
    public void cancelLongTasks() {
        taskService.cancelLongTasks();
        log.debug("Scheduled long tasks canceling performed without exceptions");
    }
}
