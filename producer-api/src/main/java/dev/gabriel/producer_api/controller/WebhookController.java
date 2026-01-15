package dev.gabriel.producer_api.controller;

import dev.gabriel.producer_api.model.WebhookEvent;
import dev.gabriel.producer_api.service.WebhookProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for ingesting Webhook events.
 * <p>
 * This controller acts as the entry point for the Event-Driven Architecture.
 * It offloads the processing to Kafka immediately, returning a generic acknowledgment.
 * </p>
 */
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookProducerService producerService;

    /**
     * Receives a webhook event and publishes it to the Kafka topic.
     * <p>
     * Returns HTTP 202 (Accepted) to indicate that the request has been received
     * for processing but has not been completed yet (Asynchronous pattern).
     * </p>
     *
     * @param event The webhook payload (validated via @Valid).
     * @return Confirmation with the Event ID.
     */
    @PostMapping
    public ResponseEntity<String> createWebhook(@RequestBody @Valid WebhookEvent event) {
        producerService.sendWebhook(event);
        return ResponseEntity.accepted().body("Webhook event sent successfully with ID: " + event.getEventId());
    }
}