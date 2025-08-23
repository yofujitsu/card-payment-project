package ru.yofujitsu.logging_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.logging_service.dto.LogEntryDto;
import ru.yofujitsu.logging_service.dto.LogLevel;
import ru.yofujitsu.logging_service.mapper.LoggingMapper;
import ru.yofujitsu.logging_service.model.LogEntry;
import ru.yofujitsu.logging_service.service.LoggingService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoggingController.class)
class LoggingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoggingService loggingService;

    @MockitoBean
    private LoggingMapper loggingMapper;

    /**
     * Тест проверяет GET-запрос /logs с параметром level.
     * Ожидаем: возвращается список логов в формате JSON.
     */
    @Test
    void logsGet_shouldReturnLogsByLevel() throws Exception {
        LogEntry entry = new LogEntry();
        entry.setLevel("INFO");
        entry.setMessage("test message");
        entry.setService("test-service");
        entry.setTimestamp(OffsetDateTime.now());

        when(loggingService.getLogsByLevel("INFO")).thenReturn(List.of(entry));

        mockMvc.perform(get("/logs").param("level", "INFO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].level").value("INFO"))
                .andExpect(jsonPath("$[0].message").value("test message"));
    }

    /**
     * Тест проверяет POST-запрос /logs с телом лога.
     * Ожидаем: сервис вызывается для сохранения и возвращается статус 201.
     */
    @Test
    void logsPost_shouldSaveLogAndReturnCreated() throws Exception {
        LogEntry entry = new LogEntry();
        entry.setLevel("ERROR");
        entry.setMessage("error message");
        entry.setService("test-service");
        entry.setTimestamp(OffsetDateTime.now());

        LogEntryDto dto = new LogEntryDto(entry.getTimestamp(), LogLevel.ERROR, "error message", "test-service");

        when(loggingMapper.toLogEntryDto(any(LogEntry.class))).thenReturn(dto);

        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isCreated());
    }
}
