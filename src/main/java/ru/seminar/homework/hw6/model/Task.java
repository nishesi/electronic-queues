package ru.seminar.homework.hw6.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.seminar.homework.hw6.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String number;
    private TaskStatus status;
    private LocalDateTime lastChangedAt;
    // Duration sets during status updating
    // Duration = "old duration" + "interval between now and lastChangedAt"
    private Map<TaskStatus, Duration> times;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (o.getClass() != getClass())
            return false;
        return number.equals(((Task) o).number);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
