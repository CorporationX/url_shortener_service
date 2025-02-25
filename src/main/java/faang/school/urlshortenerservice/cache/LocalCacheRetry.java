package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.CacheEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Queue;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCacheRetry {
    private final MessageSource messageSource;

    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public String getCachedHash(Queue<String> hashes) {
        log.info("There were no hashes in the cache. Retry to get hash. size is {}", hashes.size());

        String outHash = hashes.poll();

        if (outHash != null) {
            return outHash;
        }

        throw new CacheEmptyException(
                messageSource.getMessage("exception.cache.empty",
                        null,
                        LocaleContextHolder.getLocale()));
    }
}