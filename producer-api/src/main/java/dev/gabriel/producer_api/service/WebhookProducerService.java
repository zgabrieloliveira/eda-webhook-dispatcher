package dev.gabriel.producer_api.service;

import dev.gabriel.producer_api.model.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for publishing events to the Kafka Broker.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookProducerService {

    private final KafkaTemplate<String, WebhookEvent> kafkaTemplate;
    private static final String TOPIC = "webhook.deliveries";

    /**
     * Sends the webhook event to the configured Kafka topic.
     * <p>
     * This method uses the {@code clientId} as the Kafka Message Key to ensure
     * that all events from the same client land on the same partition,
     * guaranteeing processing order.
     * </p>
     *
     * @param event The event data to be published.
     */
    public void sendWebhook(WebhookEvent event) {
        // Idempotency/Tracing: ensure eventId is set
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        log.info("Sending event {} for client {} to topic {}", event.getEventId(), event.getClientId(), TOPIC);

        // Key = clientId (Guarantees Order), Value = Event
        kafkaTemplate.send(TOPIC, event.getClientId(), event);
    }
}