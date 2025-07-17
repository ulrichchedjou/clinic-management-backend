package com.center.clinicManagementSystem.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MetricsService {

    private final Counter loginAttemptsCounter;
    private final Counter failedLoginAttemptsCounter;
    private final Counter appointmentCreatedCounter;
    private final Counter appointmentCancelledCounter;
    private final Timer appointmentProcessingTimer;

    public MetricsService(MeterRegistry registry) {
        // Compteurs pour les tentatives de connexion
        this.loginAttemptsCounter = Counter.builder("app.auth.login.attempts")
                .description("Nombre total de tentatives de connexion")
                .register(registry);

        this.failedLoginAttemptsCounter = Counter.builder("app.auth.login.failures")
                .description("Nombre total d'échecs de connexion")
                .register(registry);

        // Compteurs pour les rendez-vous
        this.appointmentCreatedCounter = Counter.builder("app.appointments.created")
                .description("Nombre total de rendez-vous créés")
                .register(registry);

        this.appointmentCancelledCounter = Counter.builder("app.appointments.cancelled")
                .description("Nombre total de rendez-vous annulés")
                .register(registry);

        // Timer pour le traitement des rendez-vous
        this.appointmentProcessingTimer = Timer.builder("app.appointments.processing.time")
                .description("Temps de traitement des rendez-vous")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    public void incrementLoginAttempts() {
        loginAttemptsCounter.increment();
    }

    public void incrementFailedLoginAttempts() {
        failedLoginAttemptsCounter.increment();
    }

    public void incrementAppointmentCreated() {
        appointmentCreatedCounter.increment();
    }

    public void incrementAppointmentCancelled() {
        appointmentCancelledCounter.increment();
    }

    public void recordAppointmentProcessingTime(Runnable runnable) {
        appointmentProcessingTimer.record(runnable);
    }

    public void recordAppointmentProcessingTime(long duration, TimeUnit unit) {
        appointmentProcessingTimer.record(duration, unit);
    }
}
