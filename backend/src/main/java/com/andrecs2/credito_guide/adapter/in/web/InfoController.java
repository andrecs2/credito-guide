package com.andrecs2.credito_guide.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/info")
public class InfoController {
    
    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);
    
    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired(required = false)
    private HealthEndpoint healthEndpoint;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${app.name:Crédito Guide}")
    private String appName;
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${spring.kafka.bootstrap-servers:}")
    private String kafkaBootstrapServers;
    
    @Value("${kafka.topic.consulta:consulta-realizada}")
    private String topicConsulta;
    
    private final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper = new ObjectMapper();
 
    @GetMapping
    public ResponseEntity<Map<String, Object>> getInfo() {
        logger.debug("Info endpoint solicitado");
        
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", appName);
        info.put("version", appVersion);
        info.put("timestamp", LocalDateTime.now().format(formatter));
        info.put("environment", getEnvironment());
        info.put("status", "running");
        
        Map<String, Object> components = new LinkedHashMap<>();
        components.put("database", getDatabaseInfo());
        components.put("kafka", getKafkaInfo());
        components.put("health", getHealthSummary());
        
        info.put("components", components);
        info.put("endpoints", getAvailableEndpoints());
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/test-kafka")
    public ResponseEntity<Map<String, Object>> testKafka() {
        logger.info("Teste de Kafka solicitado");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now().format(formatter));
        response.put("service", appName);
        
        boolean isKafkaConfigured = isKafkaConfigured();
        
        if (!isKafkaConfigured) {
            response.put("status", "NOT_CONFIGURED");
            response.put("message", "Kafka não está configurado");
            response.put("config", Map.of(
                "bootstrapServers", kafkaBootstrapServers,
                "configured", isKafkaConfigured
            ));
            return ResponseEntity.ok(response);
        }
        
        if (kafkaTemplate == null) {
            response.put("status", "TEMPLATE_NOT_AVAILABLE");
            response.put("message", "KafkaTemplate não disponível");
            return ResponseEntity.ok(response);
        }
        
        try {
            Map<String, Object> mensagem = new HashMap<>();
            mensagem.put("test", true);
            mensagem.put("service", appName);
            mensagem.put("timestamp", System.currentTimeMillis());
            mensagem.put("event", "HEALTH_CHECK");
            
            String mensagemJson = objectMapper.writeValueAsString(mensagem);
            
            long startTime = System.currentTimeMillis();
            var future = kafkaTemplate.send(topicConsulta, "health-check", mensagemJson);
            var result = future.get(3, TimeUnit.SECONDS);
            long responseTime = System.currentTimeMillis() - startTime;
            
            response.put("status", "SUCCESS");
            response.put("message", "Mensagem enviada com sucesso");
            response.put("topic", topicConsulta);
            response.put("responseTime", responseTime + "ms");
            response.put("offset", result.getRecordMetadata().offset());
            response.put("partition", result.getRecordMetadata().partition());
            
            logger.info("✅ Teste Kafka: Mensagem enviada para tópico {} ({}ms)", 
                topicConsulta, responseTime);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("topic", topicConsulta);
            
            logger.error("❌ Teste Kafka falhou", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/kafka-status")
    public ResponseEntity<Map<String, Object>> getKafkaStatus() {
        logger.debug("Status do Kafka solicitado");
        
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("timestamp", LocalDateTime.now().format(formatter));
        status.put("service", appName);
        
        boolean isConfigured = isKafkaConfigured();
        status.put("configured", isConfigured);
        status.put("bootstrapServers", kafkaBootstrapServers);
        status.put("topic", topicConsulta);
        
        if (!isConfigured) {
            status.put("status", "NOT_CONFIGURED");
            status.put("message", "Kafka não está configurado (bootstrap-servers vazio)");
            return ResponseEntity.ok(status);
        }
        
        if (kafkaTemplate == null) {
            status.put("status", "TEMPLATE_NOT_AVAILABLE");
            status.put("message", "KafkaTemplate não disponível");
            return ResponseEntity.ok(status);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            var partitions = kafkaTemplate.partitionsFor(topicConsulta);
            long responseTime = System.currentTimeMillis() - startTime;
            
            status.put("status", "CONNECTED");
            status.put("responseTime", responseTime + "ms");
            status.put("partitions", partitions.size());
            
            logger.debug("Status Kafka: CONNECTED ({}ms)", responseTime);
            
        } catch (Exception e) {
            status.put("status", "DISCONNECTED");
            status.put("error", e.getMessage());
            status.put("errorType", e.getClass().getSimpleName());
            
            logger.warn("Status Kafka: DISCONNECTED - {}", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/simulate-notification")
    public ResponseEntity<Map<String, Object>> simulateNotification(
            @RequestBody(required = false) Map<String, String> request) {
        
        logger.info("Simulação de notificação solicitada");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now().format(formatter));
        response.put("service", appName);
        
        String cpf = "000.000.000-00";
        String tipo = "CONSULTA_REALIZADA";
        String descricao = "Consulta de crédito realizada";
        
        if (request != null) {
            cpf = request.getOrDefault("cpf", cpf);
            tipo = request.getOrDefault("tipo", tipo);
            descricao = request.getOrDefault("descricao", descricao);
        }
        
        boolean isKafkaConfigured = isKafkaConfigured();
        
        if (!isKafkaConfigured || kafkaTemplate == null) {
            response.put("status", "SIMULATED");
            response.put("message", "Kafka não configurado - notificação apenas simulada");
            response.put("data", Map.of(
                "cpf", cpf,
                "tipo", tipo,
                "descricao", descricao,
                "kafkaConfigured", isKafkaConfigured
            ));
            
            logger.info("📝 Notificação simulada - CPF: {}, Tipo: {}", cpf, tipo);
            
            return ResponseEntity.ok(response);
        }
        
        try {
            Map<String, Object> notificacao = new HashMap<>();
            notificacao.put("event", tipo);
            notificacao.put("cpf", cpf);
            notificacao.put("service", appName);
            notificacao.put("timestamp", System.currentTimeMillis());
            notificacao.put("descricao", descricao);
            notificacao.put("simulated", true);
            
            String mensagemJson = objectMapper.writeValueAsString(notificacao);
            
            long startTime = System.currentTimeMillis();
            var future = kafkaTemplate.send(topicConsulta, cpf, mensagemJson);
            var result = future.get(3, TimeUnit.SECONDS);
            long responseTime = System.currentTimeMillis() - startTime;
            
            response.put("status", "SENT");
            response.put("message", "Notificação simulada enviada para Kafka");
            response.put("data", Map.of(
                "cpf", cpf,
                "tipo", tipo,
                "topic", topicConsulta,
                "offset", result.getRecordMetadata().offset(),
                "partition", result.getRecordMetadata().partition(),
                "responseTime", responseTime + "ms"
            ));
            
            logger.info("✅ Notificação simulada enviada - CPF: {}, Tópico: {} ({}ms)", 
                cpf, topicConsulta, responseTime);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            response.put("data", Map.of(
                "cpf", cpf,
                "tipo", tipo
            ));
            
            logger.error("❌ Erro ao enviar notificação simulada", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    
    private String getEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null ? profile : "default";
    }
    
    private Map<String, Object> getDatabaseInfo() {
        Map<String, Object> db = new LinkedHashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            db.put("status", "CONNECTED");
            db.put("type", conn.getMetaData().getDatabaseProductName());
            db.put("version", conn.getMetaData().getDatabaseProductVersion());
            db.put("url", maskPassword(conn.getMetaData().getURL()));
            
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM credito", Integer.class);
                db.put("creditosCount", count);
            } catch (Exception e) {
                db.put("creditosCount", "table not found");
            }
            
        } catch (Exception e) {
            db.put("status", "DISCONNECTED");
            db.put("error", e.getMessage());
        }
        
        return db;
    }
    
    private Map<String, Object> getKafkaInfo() {
        Map<String, Object> kafka = new LinkedHashMap<>();
        
        String bootstrapServers = kafkaBootstrapServers;
        boolean isConfigured = isKafkaConfigured();
        
        kafka.put("configured", isConfigured);
        kafka.put("bootstrapServers", bootstrapServers != null ? bootstrapServers : "");
        kafka.put("topic", topicConsulta);
        
        if (!isConfigured) {
            kafka.put("status", "NOT_CONFIGURED");
            kafka.put("message", "Kafka não está configurado");
            return kafka;
        }
        
        if (kafkaTemplate == null) {
            kafka.put("status", "TEMPLATE_NOT_AVAILABLE");
            kafka.put("message", "KafkaTemplate não disponível (mas configurado)");
            return kafka;
        }
        
        try {
            kafka.put("status", "CONFIGURED");
            kafka.put("message", "Kafka configurado e disponível");
            kafka.put("test", "connection test deferred for startup performance");
            
        } catch (Exception e) {
            kafka.put("status", "ERROR");
            kafka.put("error", e.getMessage());
        }
        
        return kafka;
    }
    private Map<String, Object> getHealthSummary() {
        Map<String, Object> health = new LinkedHashMap<>();
        
        if (healthEndpoint != null) {
            try {
                HealthComponent healthComponent = healthEndpoint.health();
                health.put("status", healthComponent.getStatus().getCode());
                health.put("actuator", "available");
                
                String componentType = healthComponent.getClass().getSimpleName();
                health.put("componentType", componentType);
                
                if (healthComponent instanceof org.springframework.boot.actuate.health.Health) {
                    var healthObj = (org.springframework.boot.actuate.health.Health) healthComponent;
                    Map<String, Object> details = healthObj.getDetails();
                    if (details != null) {
                        health.put("components", details.keySet());
                    }
                } else if (healthComponent instanceof org.springframework.boot.actuate.health.CompositeHealth) {
                    var compositeHealth = (org.springframework.boot.actuate.health.CompositeHealth) healthComponent;
                    Map<String, HealthComponent> components = compositeHealth.getComponents();
                    if (components != null) {
                        health.put("components", components.keySet());
                    }
                } else {
                    health.put("componentsInfo", "type: " + componentType);
                }
                
            } catch (Exception e) {
                health.put("status", "ERROR");
                health.put("error", e.getMessage());
                logger.error("Erro ao obter health summary", e);
            }
        } else {
            health.put("status", "NOT_AVAILABLE");
            health.put("message", "HealthEndpoint não injetado");
        }
        
        return health;
    }
    
    private Map<String, String> getAvailableEndpoints() {
        Map<String, String> endpoints = new LinkedHashMap<>();
        
        endpoints.put("info", "/info");
        endpoints.put("info_kafka_test", "/info/test-kafka");
        endpoints.put("info_kafka_status", "/info/kafka-status");
        endpoints.put("info_simulate_notification", "/info/simulate-notification [POST]");
        endpoints.put("health", "/health");
        endpoints.put("health_db", "/health/database");
        endpoints.put("health_simple", "/health/simple");
        endpoints.put("actuator_health", "/actuator/health");
        endpoints.put("creditos", "/api/creditos");
        
        return endpoints;
    }
    
    private String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll("password=[^&]*", "password=****");
    }
    
    private boolean isKafkaConfigured() {
        return kafkaBootstrapServers != null && 
               !kafkaBootstrapServers.trim().isEmpty() &&
               !kafkaBootstrapServers.equals("${KAFKA_BOOTSTRAP_SERVERS:}");
    }
}