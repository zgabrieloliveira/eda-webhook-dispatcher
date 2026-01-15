package dev.gabriel.consumer_worker.service;

import dev.gabriel.consumer_worker.model.WebhookDeliveryLog;
import dev.gabriel.consumer_worker.model.WebhookEvent;
import dev.gabriel.consumer_worker.repository.WebhookDeliveryLogRepository;
import dev.gabriel.consumer_worker.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * service responsible for persisting webhook delivery logs to the database.
 * <p>
 * it handles the conversion of event payloads to json and creates
 * {@link WebhookDeliveryLog} entries.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WebhookLogService {

    private final WebhookDeliveryLogRepository repository;
    private final JsonUtil jsonUtil;

    /**
     * saves a log entry for a webhook delivery attempt.
     *
     * @param event   the original webhook event.
     * @param status  the http status code returned (or 0 for network errors).
     * @param success whether the delivery was successful.
     * @param error   the error message (if any).
     */
    public void saveLog(WebhookEvent event, int status, boolean success, String error) {

        // safely convert the payload map to a json string
        String jsonPayload = jsonUtil.toJson(event.getPayload());

        var logEntry = WebhookDeliveryLog.builder()
                .eventId(event.getEventId())
                .targetUrl(event.getTargetUrl())
                .requestPayload(jsonPayload)
                .responseStatus(status)
                .success(success)
                .errorMessage(error)
                .attemptTime(LocalDateTime.now())
                .build();

        repository.save(logEntry);
    }
}