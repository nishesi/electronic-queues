package ru.seminar.homework.hw6.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.seminar.homework.hw6.enums.TaskStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.seminar.homework.hw6.enums.TaskStatus.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@EnableAutoConfiguration
public class ManagerControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private String createTaskAndGetId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/task")).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).isNotNull().isNotBlank();
        return JsonPath.parse(body).read("$.number");
    }

    @Nested
    public class test_endpoint_GET_times {

        @Test
        public void should_return_status_ok_and_time() throws Exception {
            mockMvc.perform(get("/times"))
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(
                            jsonPath("$.time").exists())
                    .andExpect(
                            jsonPath("$.time").isNumber());
        }
    }

    @Nested
    public class test_endpoint_GET_times_status {
        @Test
        public void should_return_status_ok_and_time() throws Exception {
            TaskStatus[] statuses = {NEW, WAITING, PROCESSED};

            for (var status : statuses) {
                mockMvc.perform(get("/times/status")
                                .queryParam("status", status.name()))
                        .andExpect(
                                status().isOk())
                        .andExpect(
                                header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                        .andExpect(
                                jsonPath("$.time").exists())
                        .andExpect(
                                jsonPath("$.time").isNumber());
            }
        }

        @Test
        public void should_return_status_bad_request() throws Exception {
            TaskStatus[] statuses = {CANCEL, CLOSE};

            for (var status : statuses) {
                mockMvc.perform(get("/times/status")
                                .queryParam("status", status.name()))
                        .andExpect(
                                status().isBadRequest())
                        .andExpect(
                                header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));
            }
        }
    }

    @Nested
    public class test_endpoint_GET_times_number {
        @Test
        public void should_return_not_found_on_unknown_number() throws Exception {
            mockMvc.perform(get("/times/number")
                            .queryParam("number", "ABC123"))
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.code").value("404"))
                    .andExpect(
                            jsonPath("$.message").exists());
        }

        @Test
        public void should_return_ok_and_time_on_existing_number() throws Exception {
            String id = createTaskAndGetId();

            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/task"));
            }

            mockMvc.perform(get("/times/number")
                            .queryParam("number", id))
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(
                            jsonPath("$.time").exists())
                    .andExpect(
                            jsonPath("$.time").isNumber());
        }
    }

    @Nested
    public class test_endpoint_GET_times_$number_$status {
        @Test
        public void should_return_not_found_on_unknown_number() throws Exception {
            mockMvc.perform(get("/times/ABC123/NEW"))
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.code").value("404"))
                    .andExpect(
                            jsonPath("$.message").exists());
        }

        @Test
        public void should_return_status_bad_request_on_unknown_or_illegal_status() throws Exception {
            String id = createTaskAndGetId();

            String[] statuses = {CANCEL.name(), CLOSE.name(), "UNKNOWN_STATUS"};

            for (var status : statuses) {
                mockMvc.perform(get(
                                "/times/%s/%s".formatted(id, status)
                        ))
                        .andExpect(
                                status().isBadRequest())
                        .andExpect(
                                header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));
            }
        }

        @Test
        public void should_return_ok_and_time() throws Exception {
            String id = createTaskAndGetId();

            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/task"));
            }


            TaskStatus[] statuses = {NEW, WAITING, PROCESSED};

            for (var status : statuses) {
                mockMvc.perform(get(
                                "/times/%s/%s".formatted(id, status.name())
                        ))
                        .andExpect(
                                status().isOk())
                        .andExpect(
                                header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                        .andExpect(
                                jsonPath("$.time").exists())
                        .andExpect(
                                jsonPath("$.time").isNumber());
            }
        }
    }
}
