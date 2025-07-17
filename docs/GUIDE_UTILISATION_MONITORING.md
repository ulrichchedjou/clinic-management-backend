# Guide d'Utilisation du Système de Monitoring et de Rapports

## Table des matières
1. [Introduction](#introduction)
2. [Accès aux tableaux de bord](#acces-tableaux-de-bord)
3. [Métriques disponibles](#metriques-disponibles)
4. [Génération de rapports](#generation-rapports)
5. [Exportation des données](#exportation-donnees)
6. [Configuration avancée](#configuration-avancee)
7. [Dépannage](#depannage)

## <a name="introduction"></a>1. Introduction

Ce guide explique comment utiliser le système de monitoring et de rapports intégré à l'application de gestion de clinique. Ce système vous permet de :

- Visualiser les métriques en temps réel
- Générer des rapports personnalisés
- Exporter des données pour analyse externe
- Surveiller les performances du système

## <a name="acces-tableaux-de-bord"></a>2. Accès aux tableaux de bord

### 2.1 Tableau de bord Actuator

Le tableau de bord Actuator fournit des informations détaillées sur l'état de l'application :

```
GET /management
```

Endpoints disponibles :
- `/management/health` : État de santé de l'application
- `/management/metrics` : Métriques système détaillées
- `/management/prometheus` : Métriques au format Prometheus
- `/management/httptrace` : Dernières requêtes HTTP

### 2.2 Interface Swagger/OpenAPI

Pour accéder à la documentation interactive de l'API :

```
GET /swagger-ui.html
```

## <a name="metriques-disponibles"></a>3. Métriques disponibles

### 3.1 Métriques système

- **jvm.memory.used** : Mémoire JVM utilisée
- **system.cpu.usage** : Utilisation du CPU
- **http.server.requests** : Statistiques des requêtes HTTP
- **process.uptime** : Temps de fonctionnement du service

### 3.2 Métriques personnalisées

- **app.user.logins** : Nombre de connexions utilisateur
- **app.appointments.created** : Rendez-vous créés
- **app.appointments.completed** : Rendez-vous terminés
- **app.appointments.cancelled** : Rendez-vous annulés

## <a name="generation-rapports"></a>4. Génération de rapports

### 4.1 Types de rapports

1. **Statistiques des rendez-vous**
   ```
   GET /api/reports/appointments/stats?startDate=2025-01-01&endDate=2025-12-31
   ```

2. **Activité des utilisateurs**
   ```
   GET /api/reports/user-activity?startDate=2025-01-01&endDate=2025-12-31
   ```

3. **Performances du système**
   ```
   GET /api/reports/system-performance
   ```

## <a name="exportation-donnees"></a>5. Exportation des données

### 5.1 Format PDF

```
GET /api/reports/export/pdf?startDate=2025-01-01&endDate=2025-12-31&reportType=APPOINTMENT_STATS
```

### 5.2 Format Excel

```
GET /api/reports/export/excel?startDate=2025-01-01&endDate=2025-12-31&reportType=APPOINTMENT_STATS
```

## <a name="configuration-avancee"></a>6. Configuration avancée

### 6.1 Configuration des métriques

Pour personnaliser les métriques, modifiez le fichier `application-metrics.yml` :

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    enable:
      http:
        server:
          requests: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
```

## <a name="depannage"></a>7. Dépannage

### 7.1 Problèmes courants

**Problème** : Les métriques ne s'affichent pas
- Vérifiez que le profil `metrics` est activé
- Vérifiez les logs de l'application pour les erreurs

**Problème** : Impossible d'exporter les rapports
- Vérifiez les permissions de l'utilisateur
- Vérifiez que les dates sont valides

### 7.2 Support

Pour toute question ou problème, contactez :
- Email : support@clinic-management.com
- Téléphone : +XX X XX XX XX XX

---
*Dernière mise à jour : Juillet 2025*
