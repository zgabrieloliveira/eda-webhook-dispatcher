package dev.gabriel.consumer_worker.service;

import dev.gabriel.consumer_worker.model.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * kafka consumer responsible for listening to webhook events.
 * <p>
 * it consumes messages from the configured topic and delegates the processing
 * to the {@link WebhookDeliveryService}.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookListener {

    private final WebhookDeliveryService deliveryService;

    /**
     * consumes messages from the 'webhook.deliveries' topic.
     *
     * @param event the deserialized webhookevent object.
     */
    @KafkaListener(topics = "webhook.deliveries", groupId = "webhook-dispatcher-group")
    public void consume(WebhookEvent event) {
        log.info("Received event {} for client {}", event.getEventId(), event.getClientId());
        deliveryService.deliverWebhook(event);
    }
}