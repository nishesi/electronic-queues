package ru.seminar.homework.hw6.openapi.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.seminar.homework.hw6.openapi.enums.TaskStatus;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "задача")
public class TaskTimesDto {
    @Schema(description = "идентификатор задачи")
    private String id;
    @Schema(description = "текущий статус задачи")
    private TaskStatus status;
    @Schema(description = "статусы и проведенные в них задачей время")
    Map<TaskStatus, Integer> times;
}
