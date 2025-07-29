package faang.school.urlshortenerservice.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    private final MeterRegistry meterRegistry;
    private final Counter illegalIdForHashCounter;
    private final Counter illegalHashLengthCounter;
    private final Counter generatedHashTooLongCounter;

    public Base62Encoder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.illegalIdForHashCounter = Counter.builder("hash_generation.error.illegal_id")
                .description("Counts errors where non-negative ID was provided for hash generation")
                .register(meterRegistry);
        this.illegalHashLengthCounter = Counter.builder("hash_generation.error.illegal_length")
                .description("Counts errors where non-positive required hash length was provided")
                .register(meterRegistry);
        this.generatedHashTooLongCounter = Counter.builder("hash_generation.warn.too_long")
                .description("Counts warnings where generated hash exceeds required length")
                .register(meterRegistry);
    }

    public String encode(long num, int requiredLength) {
        if (num < 0) {
            log.warn("Id provided for hash generation must be non-negative.");
            illegalIdForHashCounter.increment();
            return "";
        }
        if (requiredLength <= 0) {
            log.warn("Hash required length must be positive.");
            illegalHashLengthCounter.increment();
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            sb.append(ALPHABET.charAt(0));
        } else {
            while (num > 0) {
                sb.append(ALPHABET.charAt((int) (num % BASE)));
                num /= BASE;
            }
        }
        StringBuilder encoded = new StringBuilder(sb.reverse().toString());

        while (encoded.length() < requiredLength) {
            encoded.insert(0, ALPHABET.charAt(0));
        }

        if (encoded.length() > requiredLength) {
            log.warn("Generated hash is too long for required length: {}", requiredLength);
            generatedHashTooLongCounter.increment();
            return "";
        }

        return encoded.toString();
    }
}