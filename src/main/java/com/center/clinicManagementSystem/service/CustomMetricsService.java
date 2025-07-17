package com.center.clinicManagementSystem.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

@Service
public class CustomMetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, LongAdder> customCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> gauges = new ConcurrentHashMap<>();

    public CustomMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeCoreMetrics();
    }

    private void initializeCoreMetrics() {
        // Initialisation des compteurs de base
        registerCounter("app.user.logins");
        registerCounter("app.user.logins.failed");
        registerCounter("app.user.registrations");
        registerCounter("app.appointments.created");
        registerCounter("app.appointments.completed");
        registerCounter("app.appointments.cancelled");
        
        // Initialisation des jauges
        registerGauge("app.active.users");
        registerGauge("app.active.sessions");
        
        // Initialisation des timers
        registerTimer("app.appointment.duration");
        registerTimer("app.request.processing.time");
    }

    // Méthodes pour les compteurs
    public void incrementCounter(String name, String... tags) {
        Counter counter = Counter.builder(name)
                .tags(tags)
                .register(meterRegistry);
        counter.increment();
        customCounters.computeIfAbsent(name, k -> new LongAdder()).increment();
    }

    public void incrementCounter(String name, double amount, String... tags) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment(amount);
        customCounters.computeIfAbsent(name, k -> new LongAdder()).add((long) amount);
    }

    // Méthodes pour les jauges
    public void setGaugeValue(String name, double value, String... tags) {
        Gauge.builder(name, () -> value)
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
        gauges.computeIfAbsent(name, k -> new AtomicInteger(0)).set((int) value);
    }

    // Méthodes pour les timers
    public void recordTime(String name, long duration, TimeUnit unit, String... tags) {
        Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(duration, unit);
    }

    public <T> T recordTime(String name, TimeSupplier<T> supplier, String... tags) throws Exception {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return supplier.get();
        } finally {
            sample.stop(Timer.builder(name)
                    .tags(tags)
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .publishPercentileHistogram()
                    .register(meterRegistry));
        }
    }

    // Méthodes utilitaires
    public long getCounterValue(String name) {
        return customCounters.getOrDefault(name, new LongAdder()).longValue();
    }

    public int getGaugeValue(String name) {
        return gauges.getOrDefault(name, new AtomicInteger(0)).get();
    }

    // Enregistrement des métriques
    private void registerCounter(String name) {
        customCounters.putIfAbsent(name, new LongAdder());
    }

    private void registerGauge(String name) {
        gauges.putIfAbsent(name, new AtomicInteger(0));
    }

    private void registerTimer(String name) {
        Timer.builder(name)
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    // Interface fonctionnelle pour les opérations chronométrées
    @FunctionalInterface
    public interface TimeSupplier<T> {
        T get() throws Exception;
    }
}
