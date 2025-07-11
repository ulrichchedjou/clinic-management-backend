package com.center.clinicManagementSystem.services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendPushNotification(String email, String title, String message) {
        // Implémentation fictive : log ou future intégration avec un service réel
        System.out.printf("[Notification] To: %s | Title: %s | Message: %s%n", email, title, message);
    }
} 