package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.RetryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility component for executing operations with automatic retry on transient errors.
 * 
 * <p><b>Important:</b> Do not use for non-idempotent operations (POST, PUT, DELETE to external APIs)
 * unless they support idempotency. Retry may lead to duplicate operations.
 * 
 * <p>Retries only transient errors (network issues, temporary database errors, timeouts).
 * Does not retry validation errors, business logic errors, and other incorrect states.
 */
@Slf4j
@Component
public class RetryExecutor {

    private final RetryProperties retryProperties;
    private final RetryTemplate retryTemplate;

    public RetryExecutor(RetryProperties retryProperties) {
        this.retryProperties = retryProperties;
        this.retryTemplate = createRetryTemplate();
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        Map<Class<? extends Throwable>, Boolean> retryable = new HashMap<>();
        retryable.put(SocketTimeoutException.class, true);
        retryable.put(IOException.class, true);
        retryable.put(TransientDataAccessException.class, true);
        retryable.put(DataAccessException.class, true);
        retryable.put(ResourceAccessException.class, true);
        retryable.put(HttpServerErrorException.class, true);

        Map<Class<? extends Throwable>, Boolean> nonRetryable = new HashMap<>();
        nonRetryable.put(IllegalArgumentException.class, false);
        nonRetryable.put(NullPointerException.class, false);
        nonRetryable.put(IllegalStateException.class, false);
        nonRetryable.put(ClassCastException.class, false);
        nonRetryable.put(IndexOutOfBoundsException.class, false);

        Map<Class<? extends Throwable>, Boolean> finalMap = new HashMap<>(retryable);
        finalMap.putAll(nonRetryable);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
                retryProperties.getMaxAttempts(),
                finalMap,
                true,
                true
        );
        template.setRetryPolicy(retryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryProperties.getDelayMs());
        template.setBackOffPolicy(backOffPolicy);

        template.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> void onError(
                    RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

                int attempt = context.getRetryCount() + 1;
                int max = retryProperties.getMaxAttempts();
                
                if (attempt < max) {
                    log.warn("Operation failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                            attempt, max, retryProperties.getDelayMs(), throwable.toString());
                } else {
                    log.warn("Operation failed (attempt {}/{}). No more retries. Error: {}",
                            attempt, max, throwable.toString());
                }
            }
        });

        return template;
    }

    public <T> T execute(Supplier<T> operation) {
        try {
            return retryTemplate.execute(context -> operation.get());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Operation failed after {} attempts", retryProperties.getMaxAttempts(), e);
            throw new RuntimeException("Operation failed after " + retryProperties.getMaxAttempts() + " attempts", e);
        }
    }

    public void execute(Runnable operation) {
        execute(() -> {
            operation.run();
            return null;
        });
    }
}