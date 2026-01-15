package dev.gabriel.consumer_worker.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing the Webhook event payload.
 * <p>
 * This class implements {@link Serializable} to ensure compatibility with
 * various Kafka serializers, although primarily JSON use.
 * Validation annotations are used to enforce the "Fail Fast" principle
 * at the Controller level.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEvent implements Serializable {

    /** Unique identifier for tracing purposes. Generated if null. */
    private String eventId;

    /** * The Client ID is critical for the Kafka Partitioning Key.
     * Events with the same clientId are guaranteed to be processed in order.
     */
    @NotBlank(message = "Client ID is mandatory for partitioning logic.")
    private String clientId;

    @NotBlank(message = "Target URL is mandatory.")
    private String targetUrl;

    private Map<String, Object> payload;
}
