package com.center.clinicManagementSystem.controller;

import lombok.extern.slf4j.Slf4j;
import com.center.clinicManagementSystem.dto.report.ChartData;
import com.center.clinicManagementSystem.dto.report.ReportRequest;
import com.center.clinicManagementSystem.responses.ApiResponse;
import com.center.clinicManagementSystem.service.ChartService;
import com.center.clinicManagementSystem.service.ReportExportService;
import com.center.clinicManagementSystem.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Rapports et Analyses", 
     description = "API pour la génération de rapports et analyses statistiques")
public class ReportController {

    private final ReportService reportService;
    private final ChartService chartService;
    private final ReportExportService reportExportService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Générer un rapport personnalisé",
              description = "Génère un rapport basé sur le type et la période spécifiés",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Rapport généré avec succès",
                             content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Requête invalide"),
                  @ApiResponse(responseCode = "403", description = "Accès non autorisé")
              })
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> generateReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Paramètres de génération du rapport",
                required = true,
                content = @Content(schema = @Schema(implementation = ReportRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ReportRequest request) {
        return reportService.generateReport(request)
                .thenApply(report -> ResponseEntity.ok(
                        ApiResponse.success(report, "Rapport généré avec succès")
                ));
    }

    @GetMapping("/appointments/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DOCTOR')")
    @Operation(summary = "Obtenir les statistiques des rendez-vous",
              description = "Récupère les statistiques des rendez-vous pour une période donnée",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
                  @ApiResponse(responseCode = "400", description = "Paramètres de date invalides")
              })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentStats(
            @Parameter(description = "Date de début (format: YYYY-MM-DD)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("La date de début doit être antérieure à la date de fin")
            );
        }
        
        ReportRequest request = new ReportRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReportType(ReportRequest.ReportType.APPOINTMENT_STATS);
        
        Map<String, Object> report = reportService.generateAppointmentStats(request);
        return ResponseEntity.ok(ApiResponse.success(report, "Statistiques des rendez-vous récupérées"));
    }

    @GetMapping("/system/performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Obtenir les métriques de performance du système",
              description = "Récupère les métriques de performance du système pour les 7 derniers jours",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Métriques récupérées avec succès")
              })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemPerformance() {
        ReportRequest request = new ReportRequest();
        request.setStartDate(LocalDate.now().minusDays(7));
        request.setEndDate(LocalDate.now());
        request.setReportType(ReportRequest.ReportType.SYSTEM_PERFORMANCE);
        
        Map<String, Object> report = reportService.generateSystemPerformanceReport(request);
        return ResponseEntity.ok(ApiResponse.success(report, "Métriques de performance récupérées"));
    }

    @GetMapping("/charts/appointments-by-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DOCTOR')")
    @Operation(summary = "Obtenir les données pour le graphique des rendez-vous par statut",
              description = "Génère les données pour un graphique circulaire des rendez-vous par statut",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Données du graphique récupérées avec succès")
              })
    public ResponseEntity<ApiResponse<ChartData>> getAppointmentsByStatusChart(
            @Parameter(description = "Date de début (format: YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: YYYY-MM-DD)", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate defaultEndDate = LocalDate.now();
        LocalDate defaultStartDate = defaultEndDate.minusMonths(1);
        
        ChartData chartData = chartService.generateAppointmentsByStatusChart(
                startDate != null ? startDate : defaultStartDate,
                endDate != null ? endDate : defaultEndDate
        );
        
        return ResponseEntity.ok(ApiResponse.success(chartData, "Données du graphique récupérées avec succès"));
    }

    @GetMapping("/charts/appointments-over-time")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DOCTOR')")
    @Operation(summary = "Obtenir les données pour le graphique d'évolution des rendez-vous",
              description = "Génère les données pour un graphique d'évolution du nombre de rendez-vous dans le temps",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Données du graphique récupérées avec succès")
              })
    public ResponseEntity<ApiResponse<ChartData>> getAppointmentsOverTimeChart(
            @Parameter(description = "Date de début (format: YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: YYYY-MM-DD)", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate defaultEndDate = LocalDate.now();
        LocalDate defaultStartDate = defaultEndDate.minusMonths(1);
        
        ChartData chartData = chartService.generateAppointmentsOverTimeChart(
                startDate != null ? startDate : defaultStartDate,
                endDate != null ? endDate : defaultEndDate
        );
        
        return ResponseEntity.ok(ApiResponse.success(chartData, "Données du graphique récupérées avec succès"));
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Exporter un rapport en PDF",
              description = "Génère et télécharge un rapport au format PDF",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Rapport PDF généré avec succès",
                             content = @Content(mediaType = "application/pdf")),
                  @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                  @ApiResponse(responseCode = "500", description = "Erreur lors de la génération du PDF")
              })
    public CompletableFuture<ResponseEntity<byte[]>> exportToPdf(
            @Parameter(description = "Date de début (format: YYYY-MM-DD)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Type de rapport à générer", required = true)
            @RequestParam ReportRequest.ReportType reportType) {
        
        if (startDate.isAfter(endDate)) {
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body("La date de début doit être antérieure à la date de fin".getBytes())
            );
        }
        
        ReportRequest request = new ReportRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReportType(reportType);
        
        return reportExportService.exportToPdf(request)
                .thenApply(pdfBytes -> {
                    String filename = String.format("rapport_%s_%s_%s.pdf", 
                            reportType.name().toLowerCase(),
                            startDate,
                            endDate);
                    
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + filename)
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdfBytes);
                })
                .exceptionally(e -> {
                    log.error("Erreur lors de la génération du PDF", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
                });
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Exporter un rapport en Excel",
              description = "Génère et télécharge un rapport au format Excel",
              responses = {
                  @ApiResponse(responseCode = "200", description = "Rapport Excel généré avec succès",
                             content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                  @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                  @ApiResponse(responseCode = "500", description = "Erreur lors de la génération du fichier Excel")
              })
    public ResponseEntity<byte[]> exportToExcel(
            @Parameter(description = "Date de début (format: YYYY-MM-DD)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Date de fin (format: YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Type de rapport à générer", required = true)
            @RequestParam ReportRequest.ReportType reportType) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body("La date de début doit être antérieure à la date de fin".getBytes());
        }
        
        ReportRequest request = new ReportRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReportType(reportType);
        
        try {
            byte[] excelBytes = reportExportService.exportToExcel(request);
            String filename = String.format("rapport_%s_%s_%s.xlsx", 
                    reportType.name().toLowerCase(),
                    startDate,
                    endDate);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
                    
        } catch (Exception e) {
            log.error("Erreur lors de la génération du fichier Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du fichier Excel: " + e.getMessage()).getBytes());
        }
    }
}
