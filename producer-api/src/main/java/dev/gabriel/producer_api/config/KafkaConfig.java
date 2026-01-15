package dev.gabriel.producer_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration class for Kafka infrastructure.
 * <p>
 * This class handles the automatic creation and configuration of Kafka topics
 * when the application starts. It ensures the infrastructure requirements
 * (like partition count) are met without manual intervention.
 * </p>
 */
@Configuration
public class KafkaConfig {

    /**
     * Creates the 'webhook.deliveries' topic if it does not exist.
     *
     * @return A NewTopic object with the specified configuration.
     */
    @Bean
    public NewTopic webhookTopic() {
        return TopicBuilder.name("webhook.deliveries")
                .partitions(3)  // parallelism and efficiency
                .replicas(1)    // local -> single node
                .build();
    }
}
