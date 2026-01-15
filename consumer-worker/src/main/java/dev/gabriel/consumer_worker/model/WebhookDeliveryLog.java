package dev.gabriel.consumer_worker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * jpa entity representing a detailed log of a webhook delivery attempt.
 * <p>
 * this entity is used for auditing, debugging, and tracking the history
 * of successful and failed deliveries.
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventId;
    private String targetUrl;
    /**
     * the json payload sent in the request body.
     * stored as text to accommodate large payloads.
     */
    @Column(columnDefinition = "TEXT")
    private String requestPayload;
    /**
     * the http status code returned by the destination (e.g., 200, 404, 500).
     * 0 indicates a connection failure or internal error.
     */
    private int responseStatus;
    private boolean success;
    /**
     * the error message or reason for failure, if applicable.
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    private LocalDateTime attemptTime;
}