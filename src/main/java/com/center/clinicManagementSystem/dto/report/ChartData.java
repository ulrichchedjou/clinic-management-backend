package com.center.clinicManagementSystem.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartData {
    private String title;
    private String type; // bar, line, pie, etc.
    private List<String> labels;
    private List<Dataset> datasets;
    private Map<String, Object> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dataset {
        private String label;
        private List<Number> data;
        private List<String> backgroundColor;
        private List<String> borderColor;
        private Integer borderWidth = 1;
    }
}
