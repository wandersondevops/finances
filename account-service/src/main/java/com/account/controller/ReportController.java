package com.account.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.account.service.ReportService;

@RestController
@RequestMapping("/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reportes")
public ResponseEntity<?> generateReport(
    @RequestParam String fecha,
    @RequestParam UUID client) {
    try {
        String[] dates = fecha.split(",");
        LocalDateTime startDate = LocalDateTime.parse(dates[0]);
        LocalDateTime endDate = LocalDateTime.parse(dates[1]);

        Map<String, Object> report = reportService.generateAccountStatement(client, startDate, endDate);
        return ResponseEntity.ok(report);
    } catch (DateTimeParseException e) {
        return ResponseEntity.badRequest().body("Invalid date format. Please use 'yyyy-MM-ddTHH:mm:ss'");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}

}
