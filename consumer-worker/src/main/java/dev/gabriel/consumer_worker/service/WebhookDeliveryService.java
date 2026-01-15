package dev.gabriel.consumer_worker.service;

import dev.gabriel.consumer_worker.model.WebhookEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

/**
 * service responsible for executing the http post request to the target url.
 * <p>
 * it implements the circuit breaker pattern using resilience4j to handle
 * failures gracefully and prevent cascading errors in the system.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookDeliveryService {

    private final RestClient restClient = RestClient.create();
    private final WebhookLogService logService;

    /**
     * attempts to deliver the webhook event to the specified target url.
     * <p>
     * this method is protected by a circuit breaker. if the failure rate exceeds
     * the configured threshold, calls will be redirected to the fallback method.
     * </p>
     *
     * @param event the webhook event data containing the payload and target url.
     */
    // "webhookDelivery" is the name configured in application.yml
    @CircuitBreaker(name = "webhookDelivery", fallbackMethod = "fallbackHandleDelivery")
    public void deliverWebhook(WebhookEvent event) {
        log.info("Trying to send event {} to destination {}", event.getEventId(), event.getTargetUrl());

        try {
            // synchronous call to the target url
            ResponseEntity<Void> response = restClient.post()
                    .uri(event.getTargetUrl())
                    .body(event.getPayload())
                    .retrieve()
                    .toBodilessEntity();

            // log success in the database
            logService.saveLog(event, response.getStatusCode().value(), true, null);
            log.info("Event {} delivered successfully to {}", event.getEventId(), event.getTargetUrl());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // handle specific http errors (4xx, 5xx)
            log.error("HTTP error delivering event {} to {}: {}",
                    event.getEventId(), event.getTargetUrl(), e.getMessage());

            logService.saveLog(event, e.getStatusCode().value(), false, e.getMessage());

            // rethrow to trigger the circuit breaker failure counting
            throw e;
        } catch (Exception e) {
            // handle generic errors (connection refused, timeout, etc.)
            log.error("Error delivering event {} to {}: {}",
                    event.getEventId(), event.getTargetUrl(), e.getMessage());

            logService.saveLog(event, 0, false, e.getMessage());

            // rethrow to trigger the circuit breaker failure counting
            throw e;
        }
    }

    /**
     * fallback method invoked when the circuit breaker is open or when a generic exception occurs.
     * <p>
     * this ensures the system records the final failure state instead of crashing or hanging.
     * </p>
     *
     * @param event the webhook event that failed to be delivered.
     * @param t     the exception that caused the failure.
     */
    public void fallbackHandleDelivery(WebhookEvent event, Throwable t) {
        log.error("Fallback: Failed to deliver event {} to {}. Reason: {}",
                event.getEventId(), event.getTargetUrl(), t.getMessage());

        // save the final failure log to the database
        logService.saveLog(event, 0, false, "FALLBACK: " + t.getMessage());
    }
}