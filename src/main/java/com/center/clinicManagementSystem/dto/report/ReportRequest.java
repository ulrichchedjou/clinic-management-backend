package com.center.clinicManagementSystem.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private ReportType reportType;
    
    public enum ReportType {
        APPOINTMENT_STATS,
        USER_ACTIVITY,
        SYSTEM_PERFORMANCE,
        ERROR_ANALYSIS
    }
}
