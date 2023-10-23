package ru.seminar.homework.hw6.enums;

import java.util.List;

public enum TaskStatus {
    NEW,
    WAITING,
    PROCESSED,
    CLOSE,
    CANCEL;

    // java guaranties that order in array like they were declared
    public static final List<TaskStatus> values = List.of(TaskStatus.values());
}
