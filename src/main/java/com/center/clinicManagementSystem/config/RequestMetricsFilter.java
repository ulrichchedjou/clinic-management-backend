package com.center.clinicManagementSystem.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
@RequiredArgsConstructor
public class RequestMetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            String path = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();

            // Enregistrer la métrique
            meterRegistry.timer("http.server.requests",
                    Collections.singletonList(
                            Tag.of("uri", path)
                    )
            ).record(duration, TimeUnit.MILLISECONDS);

            // Enregistrer les métriques par statut et méthode
            meterRegistry.counter("http.requests.total",
                    "method", method,
                    "status", String.valueOf(status),
                    "uri", path
            ).increment();

            // Copier le contenu de la réponse pour le client
            wrappedResponse.copyBodyToResponse();
        }
    }
}
