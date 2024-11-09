package com.account;

import com.account.controller.ReportController;
import com.account.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(ReportController.class)
class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private UUID clientId;
    private String validStartDate;
    private String validEndDate;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        validStartDate = "2024-11-01T00:00:00";
        validEndDate = "2024-11-09T23:59:59";
    }

    @Test
    void generateReport_ShouldReturnReportData_WhenRequestIsValid() throws Exception {
        // Arrange
        Map<String, Object> mockReportData = new HashMap<>();
        mockReportData.put("client", "Mock Client");
        mockReportData.put("accounts", "Mock Accounts");

        when(reportService.generateAccountStatement(eq(clientId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockReportData);

        // Act & Assert
        mockMvc.perform(get("/reportes/reportes")
                        .param("fecha", validStartDate + "," + validEndDate)
                        .param("client", clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"client\":\"Mock Client\",\"accounts\":\"Mock Accounts\"}"));
    }

    @Test
    void generateReport_ShouldReturnBadRequest_WhenDateFormatIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/reportes/reportes")
                        .param("fecha", "invalidDateFormat")
                        .param("client", clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid date format. Please use 'yyyy-MM-ddTHH:mm:ss'"));
    }

    @Test
    void generateReport_ShouldReturnServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(reportService.generateAccountStatement(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(get("/reportes/reportes")
                        .param("fecha", validStartDate + "," + validEndDate)
                        .param("client", clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred."));
    }
}

