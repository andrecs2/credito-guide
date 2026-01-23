package com.andrecs2.credito_guide.adapter.in.web;

import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired(required = false)
    private Optional<KafkaAdmin> kafkaAdmin;
    
    @Value("${app.name:Crédito Guide}")
    private String appName;
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    private final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        logger.debug("Health check solicitado");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", appName);
        response.put("version", appVersion);
        response.put("timestamp", LocalDateTime.now().format(formatter));
        
        Map<String, Object> dbStatus = checkDatabase();
        Map<String, Object> kafkaStatus = checkKafka();
        
        String overallStatus = determineOverallStatus(dbStatus, kafkaStatus);
        response.put("status", overallStatus);
        
        Map<String, Object> components = new LinkedHashMap<>();
        components.put("application", checkApplication());
        components.put("database", dbStatus);
        components.put("kafka", kafkaStatus);
        components.put("system", checkSystem());
        
        response.put("components", components);
        
        logger.info("Health check retornado: status={}", overallStatus);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> simpleHealth() {
        logger.debug("Health check simples solicitado");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", appName);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        logger.debug("Health check do banco solicitado");
        return ResponseEntity.ok(checkDatabase());
    }
    
    @GetMapping("/kafka")
    public ResponseEntity<Map<String, Object>> kafkaHealth() {
        logger.debug("Health check do Kafka solicitado");
        return ResponseEntity.ok(checkKafka());
    }
        
    private Map<String, Object> checkApplication() {
        Map<String, Object> app = new LinkedHashMap<>();
        
        app.put("status", "UP");
        app.put("name", appName);
        app.put("version", appVersion);
        app.put("java", System.getProperty("java.version"));
        app.put("springBoot", getSpringBootVersion());
        
        long uptime = java.lang.management.ManagementFactory
            .getRuntimeMXBean().getUptime();
        app.put("uptime", formatUptime(uptime));
        
        return app;
    }
    
    private Map<String, Object> checkDatabase() {
        Map<String, Object> db = new LinkedHashMap<>();
        
        if (dataSource == null) {
            db.put("status", "DOWN");
            db.put("message", "DataSource não configurado");
            logger.warn("Health check DB: DataSource não configurado");
            return db;
        }
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            long start = System.currentTimeMillis();
            boolean result = stmt.execute("SELECT 1");
            long responseTime = System.currentTimeMillis() - start;
            
            db.put("status", "UP");
            db.put("type", conn.getMetaData().getDatabaseProductName());
            db.put("version", conn.getMetaData().getDatabaseProductVersion());
            db.put("responseTime", responseTime + "ms");
            db.put("test", "SELECT 1 executado com sucesso");
            
            logger.debug("Health check DB: UP ({}ms)", responseTime);
            
        } catch (Exception e) {
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
            db.put("errorType", e.getClass().getSimpleName());
            
            logger.error("Health check DB: DOWN - {}", e.getMessage());
        }
        
        return db;
    }
    
    private Map<String, Object> checkKafka() {
        Map<String, Object> kafka = new LinkedHashMap<>();
        
        if (kafkaAdmin == null || kafkaAdmin.isEmpty()) {
            kafka.put("status", "NOT_CONFIGURED");
            kafka.put("message", "Kafka não está configurado");
            kafka.put("configSource", "Nenhuma configuração encontrada");
            logger.debug("Health check Kafka: Não configurado");
            return kafka;
        }
        
        try {
            var admin = kafkaAdmin.get();
            Map<String, Object> configs = admin.getConfigurationProperties();
            
            Object serversObj = configs.get("bootstrap.servers");
            String servers = "";
            
            if (serversObj instanceof String) {
                servers = (String) serversObj;
            } else if (serversObj instanceof List) {
                List<?> serversList = (List<?>) serversObj;
                servers = String.join(",", serversList.stream()
                    .map(Object::toString)
                    .toList());
            } else if (serversObj != null) {
                servers = serversObj.toString();
            }
            
            boolean isDefaultConfig = servers == null || 
                                     servers.isEmpty() || 
                                     "localhost:9092".equals(servers);
            
            Map<String, Object> connectionTest = testKafkaConnection(admin);
            String connectionStatus = (String) connectionTest.get("status");
            
            kafka.put("status", connectionStatus);
            kafka.put("bootstrapServers", servers);
            kafka.put("configSource", isDefaultConfig ? "DEFAULT" : "ENVIRONMENT");
            kafka.put("lastChecked", Instant.now().toString());
            
            if ("CONNECTED".equals(connectionStatus)) {
                kafka.put("responseTime", connectionTest.get("responseTime"));
                kafka.put("topicCount", connectionTest.get("topicCount"));
                kafka.put("test", "listTopics successful");
                logger.info("Health check Kafka: CONNECTED to {} ({}ms)", 
                    servers, connectionTest.get("responseTime"));
            } else {
                kafka.put("error", connectionTest.get("error"));
                kafka.put("test", "connection failed");
                logger.warn("Health check Kafka: {} - {}", connectionStatus, connectionTest.get("error"));
            }
            
        } catch (Exception e) {
            kafka.put("status", "ERROR");
            kafka.put("error", e.getMessage());
            kafka.put("errorType", e.getClass().getSimpleName());
            logger.error("Health check Kafka: Erro - {}", e.getMessage());
        }
        
        return kafka;
    }

    private Map<String, Object> testKafkaConnection(KafkaAdmin admin) {
        Map<String, Object> result = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        
        try (var client = AdminClient.create(admin.getConfigurationProperties())) {
            var topicsResult = client.listTopics(
                new ListTopicsOptions().timeoutMs(3000));
            var topicNames = topicsResult.names().get(3, TimeUnit.SECONDS);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            result.put("status", "CONNECTED");
            result.put("responseTime", responseTime);
            result.put("topicCount", topicNames.size());
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            result.put("status", "DISCONNECTED");
            result.put("error", e.getMessage());
            result.put("responseTime", responseTime);
        }
        
        return result;
    }
    private Map<String, Object> checkSystem() {
        Map<String, Object> system = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        long maxMB = runtime.maxMemory() / (1024 * 1024);
        long totalMB = runtime.totalMemory() / (1024 * 1024);
        long freeMB = runtime.freeMemory() / (1024 * 1024);
        long usedMB = totalMB - freeMB;
        double usedPercent = totalMB > 0 ? ((double) usedMB / totalMB) * 100 : 0;
        
        system.put("memory", Map.of(
            "max", maxMB + " MB",
            "total", totalMB + " MB",
            "used", usedMB + " MB",
            "free", freeMB + " MB",
            "usedPercent", String.format("%.1f%%", usedPercent)
        ));
        
        system.put("processors", runtime.availableProcessors());
        system.put("os", System.getProperty("os.name"));
        
        return system;
    }
    
    private String determineOverallStatus(Map<String, Object> dbStatus, 
                                         Map<String, Object> kafkaStatus) {
        if ("DOWN".equals(dbStatus.get("status"))) {
            return "DOWN";
        }
        
        String kafkaStatusStr = (String) kafkaStatus.get("status");
        if ("ERROR".equals(kafkaStatusStr) || "MISCONFIGURED".equals(kafkaStatusStr)) {
            return "DEGRADED";
        }
        
        return "UP";
    }
    
    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%dd %dh %dm %ds", 
            days, hours % 24, minutes % 60, seconds % 60);
    }
    
    private String getSpringBootVersion() {
        try {
            return org.springframework.boot.SpringBootVersion.getVersion();
        } catch (Exception e) {
            return "unknown";
        }
    }
}