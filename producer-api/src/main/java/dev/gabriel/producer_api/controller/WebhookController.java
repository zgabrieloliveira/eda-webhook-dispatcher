package dev.gabriel.producer_api.controller;

import dev.gabriel.producer_api.model.WebhookEvent;
import dev.gabriel.producer_api.service.WebhookProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Webhook Dispatcher", description = "Endpoints for ingesting and processing webhook events")
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
    @Operation(
            summary = "Dispatch a new webhook event",
            description = "Receives the webhook payload, validates the data, and queues it in Kafka for asynchronous delivery."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Event accepted and queued successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload provided (e.g., missing target URL or client ID)"),
            @ApiResponse(responseCode = "500", description = "Internal server error (e.g., Kafka broker unavailable)")
    })
    @PostMapping
    public ResponseEntity<String> createWebhook(@RequestBody @Valid WebhookEvent event) {
        producerService.sendWebhook(event);
        return ResponseEntity.accepted().body("Webhook event sent successfully with ID: " + event.getEventId());
    }
}