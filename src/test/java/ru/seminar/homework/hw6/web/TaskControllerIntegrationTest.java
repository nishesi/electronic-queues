package ru.seminar.homework.hw6.web;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.seminar.homework.hw6.enums.TaskStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.seminar.homework.hw6.enums.TaskStatus.*;

@Log4j2
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@EnableAutoConfiguration
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String createTaskAndGetId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/task")).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).isNotNull().isNotBlank();
        return JsonPath.parse(body).read("$.number");
    }

    @Nested
    public class test_endpoint_POST_task {

        @Test
        public void should_return_status_code_201_and_task_id() throws Exception {
            mockMvc.perform(post("/task"))
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(
                            jsonPath("$.number").exists());
        }

        @Test
        public void should_return_unique_ids_for_every_request() throws Exception {
            Set<String> ids = new HashSet<>();

            for (int i = 0; i < 10; i++) {
                String id = createTaskAndGetId();
                boolean added = ids.add(id);
                assertThat(added).isTrue();
            }
        }

        @Test
        public void should_save_task_with_correct_status() throws Exception {
            mockMvc.perform(post("/task"))
                    .andExpect(
                            jsonPath("$.status").value("NEW"));
        }
    }

    @Nested
    public class test_endpoint_GET_task {
        @Test
        public void should_return_status_not_found_on_empty_queue() throws Exception {
            mockMvc.perform(get("/task"))
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.code").value("404"))
                    .andExpect(
                            jsonPath("$.message").exists());
        }

        @Test
        public void should_return_correct_task() throws Exception {
            String id = createTaskAndGetId();

            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/task"));
            }

            mockMvc.perform(patch("/task/%s".formatted(id))
                            .queryParam("status", WAITING.name()))
                    .andExpect(status().isAccepted());
            mockMvc.perform(get("/task"))
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(
                            jsonPath("$.number").value(id))
                    .andExpect(
                            jsonPath("$.status").value("WAITING"));
        }
    }

    @Nested
    public class test_endpoint_PATCH_task {
        @Test
        public void should_return_status_code_accepted_and_update_status() throws Exception {
            String id = createTaskAndGetId();

            mockMvc.perform(
                            patch("/task/%s".formatted(id))
                                    .queryParam("status", "WAITING"))
                    .andExpect(
                            status().isAccepted())
                    .andExpect(
                            jsonPath("$.number").value(id))
                    .andExpect(
                            jsonPath("$.status").value("WAITING"));
        }

        @Test
        public void should_return_validation_error_on_incorrect_number() throws Exception {
            String illegal_id = "123ABC&^$";
            mockMvc.perform(
                            patch("/task/%s".formatted(illegal_id))
                                    .queryParam("status", "WAITING"))
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.errors").exists())
                    .andExpect(
                            jsonPath("$.errors").isArray())
                    .andExpect(
                            jsonPath("$.errors").isNotEmpty())
                    .andExpect(
                            jsonPath("$.errors[*].field").value("number"))
                    .andExpect(
                            jsonPath("$.errors[*].value").value(illegal_id))
                    .andExpect(
                            jsonPath("$.errors[*].message").exists());
        }

        @Test
        public void should_return_bad_request_if_updating_of_status_is_incorrect() throws Exception {
            String id = createTaskAndGetId();

            BiConsumer<TaskStatus, ResultMatcher> checker = (status, expected) -> {
                try {
                    mockMvc.perform(
                                    patch("/task/%s".formatted(id))
                                            .queryParam("status", status.name()))
                            .andExpect(expected)
                            .andDo(print());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };


            // NEW -> WAITING -> PROCESSED -> CLOSE
            // status may be updated to another that near current
            // from any -> CANCEL

            TaskStatus[] statuses = {NEW, WAITING, PROCESSED, CLOSE};

            for (int i = 1; i < statuses.length; i++) {

                // set new status position to check
                checker.accept(statuses[i], status().isAccepted());

                for (int j = 0; j < statuses.length; j++) {

                    log.info("Current status: %s,\tUpdating status: %s\n".formatted(statuses[i], statuses[j]));
                    if (Math.abs(i - j) == 1) {
                        checker.accept(statuses[j], status().isAccepted());

                        // return old status position
                        checker.accept(statuses[i], status().isAccepted());
                    } else {
                        checker.accept(statuses[j], status().isBadRequest());
                    }
                }
            }
        }

        @Test
        public void should_return_not_found_on_unknown_number() throws Exception {
            String unknownId = "123ABC";
            mockMvc.perform(
                            patch("/task/%s".formatted(unknownId))
                                    .queryParam("status", "WAITING"))
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.code").value("404"))
                    .andExpect(
                            jsonPath("$.message").exists());
        }

        @Test
        public void should_return_bad_request_if_new_status_empty() throws Exception {
            String id = createTaskAndGetId();
            mockMvc.perform(
                            patch("/task/%s".formatted(id)))
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.errors").exists())
                    .andExpect(
                            jsonPath("$.errors").isArray())
                    .andExpect(
                            jsonPath("$.errors").isNotEmpty())
                    .andExpect(
                            jsonPath("$.errors[*].field").value("status"))
                    .andExpect(
                            jsonPath("$.errors[*].message").exists());
        }
    }

    @Nested
    public class test_endpoint_DELETE_task {
        @Test
        public void should_return_not_found_on_unknown_number() throws Exception {
            String illegalId = "123ABC";
            mockMvc.perform(
                            delete("/task/%s".formatted(illegalId)))
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.code").value("404"))
                    .andExpect(
                            jsonPath("$.message").exists());
        }

        @Test
        public void should_delete_task() throws Exception {
            String id = createTaskAndGetId();

            mockMvc.perform(
                            delete("/task/%s".formatted(id)))
                    .andExpect(
                            status().isAccepted())
                    .andExpect(
                            jsonPath("$.number").value(id));

            mockMvc.perform(
                            patch("/task/%s".formatted(id))
                                    .queryParam("status", "WAITING"))
                    .andExpect(
                            status().isNotFound());
        }
    }

    @Nested
    public class test_endpoint_GET_tasks {
        @Test
        public void should_return_empty_lists_of_numbers() throws Exception {
            ResultActions resultActions = mockMvc.perform(get("/tasks"))
                    .andExpect(
                            status().isOk());

            for (TaskStatus status : new TaskStatus[]{NEW, WAITING, PROCESSED}) {
                String query = "$.content." + status.name();

                resultActions.andExpect(
                                jsonPath(query).exists())
                        .andExpect(
                                jsonPath(query).isArray())
                        .andExpect(
                                jsonPath(query).isEmpty());
            }

            resultActions.andExpect(
                            jsonPath("$.content." + CANCEL.name()).doesNotExist()
                    )
                    .andExpect(
                            jsonPath("$.content." + CLOSE.name()).doesNotExist()
                    );
        }

        @Test
        public void should_return_correct_lists_of_numbers() throws Exception {
            String[] numbers = prepareTasksAndGetItsNumbers();

            ResultActions resultActions = mockMvc.perform(get("/tasks"))
                    .andExpect(
                            status().isOk());

            TaskStatus[] expectedStatuses = {NEW, WAITING, PROCESSED};
            for (int i = 0; i < expectedStatuses.length; i++) {
                String query = "$.content." + expectedStatuses[i].name();

                resultActions.andExpect(
                                jsonPath(query).exists())
                        .andExpect(
                                jsonPath(query).isArray())
                        .andExpect(
                                jsonPath(query).isNotEmpty())
                        .andExpect(
                                jsonPath(query + "[0]").value(numbers[i]))
                        .andExpect(
                                jsonPath(query + "[1]").doesNotExist());
            }
        }

        private String[] prepareTasksAndGetItsNumbers() {
            TaskStatus[] statuses = {NEW, WAITING, PROCESSED, CLOSE, CANCEL};

            //sets status of task to specified from array
            Function<Integer, String> prepareTaskAndGetId = (statusId) -> {
                try {
                    String id = createTaskAndGetId();
                    for (int i = 0; i < statusId; i++) {
                        mockMvc.perform(
                                        patch("/task/%s".formatted(id))
                                                .queryParam("status", statuses[i + 1].name()))
                                .andExpect(
                                        status().isAccepted());
                    }
                    return id;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new String[]{
                    prepareTaskAndGetId.apply(0),
                    prepareTaskAndGetId.apply(1),
                    prepareTaskAndGetId.apply(2),
                    prepareTaskAndGetId.apply(3),
                    prepareTaskAndGetId.apply(4),
            };
        }
    }
}
