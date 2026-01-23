package com.andrecs2.credito_guide.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaSimpleConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaSimpleConfig.class);
    
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = false)
    public KafkaAdmin kafkaAdmin() {
        String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "");
        
        if (bootstrapServers == null || bootstrapServers.trim().isEmpty()) {
            logger.info("  Kafka: Não configurado (bootstrap servers vazio)");
            return null;
        }
        
        logger.info("🔧 Kafka: Configurando para {}", bootstrapServers);
        
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        configs.put("request.timeout.ms", 3000);
        
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setFatalIfBrokerNotAvailable(false);
        
        return admin;
    }
}
