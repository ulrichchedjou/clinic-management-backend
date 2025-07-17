package com.center.clinicManagementSystem.service;

import com.center.clinicManagementSystem.dto.report.ChartData;
import com.center.clinicManagementSystem.model.Appointment;
import com.center.clinicManagementSystem.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final AppointmentRepository appointmentRepository;
    private final CustomMetricsService metricsService;

    public ChartData generateAppointmentsByStatusChart(LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<String, Long> statusCount = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().name(),
                        Collectors.counting()
                ));

        List<String> labels = new ArrayList<>(statusCount.keySet());
        List<Number> data = new ArrayList<>(statusCount.values());

        // Générer des couleurs uniques pour chaque statut
        List<String> backgroundColors = generateColors(labels.size(), 0.7);
        List<String> borderColors = generateColors(labels.size(), 1.0);

        ChartData.Dataset dataset = new ChartData.Dataset("Rendez-vous par statut",
                data, backgroundColors, borderColors, null);

        return ChartData.builder()
                .title("Répartition des rendez-vous par statut")
                .type("pie")
                .labels(labels)
                .datasets(Collections.singletonList(dataset))
                .options(Map.of(
                        "responsive", true,
                        "plugins", Map.of(
                                "legend", Map.of("position", "right"),
                                "title", Map.of(
                                        "display", true,
                                        "text", "Répartition des rendez-vous par statut"
                                )
                        )
                ))
                .build();
    }

    public ChartData generateAppointmentsOverTimeChart(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());

        List<String> labels = dateRange.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        Map<LocalDate, Long> appointmentsByDate = appointmentRepository
                .findByAppointmentDateBetween(
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().toLocalDate(),
                        Collectors.counting()
                ));

        List<Number> data = dateRange.stream()
                .map(date -> appointmentsByDate.getOrDefault(date, 0L))
                .collect(Collectors.toList());

        ChartData.Dataset dataset = new ChartData.Dataset();
        dataset.setLabel("Nombre de rendez-vous");
        dataset.setData(data);
        dataset.setBackgroundColor(Collections.singletonList("rgba(54, 162, 235, 0.5)"));
        dataset.setBorderColor(Collections.singletonList("rgba(54, 162, 235, 1)"));

        return ChartData.builder()
                .title("Évolution des rendez-vous sur la période")
                .type("line")
                .labels(labels)
                .datasets(Collections.singletonList(dataset))
                .options(Map.of(
                        "responsive", true,
                        "scales", Map.of(
                                "y", Map.of("beginAtZero", true)
                        ),
                        "plugins", Map.of(
                                "title", Map.of(
                                        "display", true,
                                        "text", "Évolution des rendez-vous sur la période"
                                )
                        )
                ))
                .build();
    }

    public ChartData generateUserActivityChart(LocalDate startDate, LocalDate endDate) {
        // Implémentation similaire pour l'activité des utilisateurs
        // ...
        return new ChartData();
    }

    private List<String> generateColors(int count, double opacity) {
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String color = String.format(
                    "rgba(%d, %d, %d, %f)",
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    opacity
            );
            colors.add(color);
        }
        return colors;
    }
}
