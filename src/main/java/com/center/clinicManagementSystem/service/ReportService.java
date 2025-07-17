package com.center.clinicManagementSystem.service;

import com.center.clinicManagementSystem.dto.report.ReportRequest;
import com.center.clinicManagementSystem.model.Appointment;
import com.center.clinicManagementSystem.enums.AppointmentStatus;
import com.center.clinicManagementSystem.model.User;
import com.center.clinicManagementSystem.repository.AppointmentRepository;
import com.center.clinicManagementSystem.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;
    private final MetricsService metricsService;

    @Async
    public CompletableFuture<Map<String, Object>> generateReport(ReportRequest request) {
        log.info("Génération du rapport de type {} pour la période {} à {}", 
                request.getReportType(), request.getStartDate(), request.getEndDate());

        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> report = new HashMap<>();
            report.put("generatedAt", LocalDateTime.now());
            report.put("reportType", request.getReportType());
            report.put("period", Map.of(
                    "startDate", request.getStartDate(),
                    "endDate", request.getEndDate()
            ));

            switch (request.getReportType()) {
                case APPOINTMENT_STATS:
                    report.put("data", generateAppointmentStats(request));
                    break;
                case USER_ACTIVITY:
                    report.put("data", generateUserActivityReport(request));
                    break;
                case SYSTEM_PERFORMANCE:
                    report.put("data", generateSystemPerformanceReport(request));
                    break;
                case ERROR_ANALYSIS:
                    report.put("data", generateErrorAnalysisReport(request));
                    break;
                default:
                    throw new IllegalArgumentException("Type de rapport non supporté: " + request.getReportType());
            }

            return report;
        });
    }

    @Cacheable(value = "appointmentReports", key = "#request.toString()")
    public Map<String, Object> generateAppointmentStats(ReportRequest request) {
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);

        long totalAppointments = appointments.size();
        long completed = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();
        long cancelled = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .count();
        long noShow = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.NO_SHOW)
                .count();

        Map<String, Long> appointmentsByStatus = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAppointments", totalAppointments);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("noShow", noShow);
        stats.put("completionRate", totalAppointments > 0 ? 
                (double) completed / totalAppointments * 100 : 0);
        stats.put("appointmentsByStatus", appointmentsByStatus);

        // Enregistrer la métrique de génération de rapport
        metricsService.recordAppointmentProcessingTime(100, TimeUnit.MILLISECONDS);

        return stats;
    }

    protected Map<String, Object> generateUserActivityReport(ReportRequest request) {
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        // Récupérer les utilisateurs actifs
        List<User> activeUsers = userRepository.findByLastLoginBetween(start, end);
        long newUsers = userRepository.countByCreatedAtBetween(start, end);

        // Récupérer les statistiques d'activité
        Map<String, Long> activityByUserType = activeUsers.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().name(),
                        Collectors.counting()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveUsers", activeUsers.size());
        stats.put("newUsers", newUsers);
        stats.put("activityByUserType", activityByUserType);

        return stats;
    }

    public Map<String, Object> generateSystemPerformanceReport(ReportRequest request) {
        Map<String, Object> metrics = new HashMap<>();

        // Récupérer les métriques de base
        metrics.put("jvm.memory.used", meterRegistry.find("jvm.memory.used").gauge().value());
        metrics.put("system.cpu.usage", meterRegistry.find("system.cpu.usage").gauge().value());
        metrics.put("http.server.requests.count", 
                meterRegistry.find("http.server.requests").counter().count());

        // Récupérer le temps de réponse moyen
        double avgResponseTime = 0.0;
        Timer timer = (Timer) meterRegistry.find("http.server.requests").timer();
        if (timer != null) {
            avgResponseTime = timer.mean(TimeUnit.MILLISECONDS);
        }
        metrics.put("avgResponseTimeMs", avgResponseTime);

        return metrics;
    }

    protected Map<String, Object> generateErrorAnalysisReport(ReportRequest request) {
        // Dans une implémentation réelle, cela pourrait interroger un service de journalisation
        // ou une base de données d'erreurs
        Map<String, Object> errorStats = new HashMap<>();
        errorStats.put("totalErrors", 0);
        errorStats.put("errorByType", Collections.emptyMap());
        errorStats.put("mostCommonErrors", Collections.emptyList());
        
        return errorStats;
    }
}
