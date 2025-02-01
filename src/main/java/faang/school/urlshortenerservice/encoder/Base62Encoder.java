package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();
    private static final int MAX_HASH_LENGTH = 6;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Base62Encoder.class);


    public List<String> encodeBatch(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Input numbers list cannot be null or empty");
        }

        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            String hash = encode(number);
            log.debug("Generated hash: {}", hash);
            hashes.add(hash);
        }
        return hashes;
    }

    public String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot encode negative numbers: " + value);
        }

        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            encoded.append(BASE62.charAt((int) (value % BASE)));
            value /= BASE;
        }


        while (encoded.length() < MAX_HASH_LENGTH) {
            encoded.insert(0, '0');
        }


        if (encoded.length() > MAX_HASH_LENGTH) {
            return encoded.substring(0, MAX_HASH_LENGTH);
        }
        log.debug("Final encoded hash: {}", encoded);
        return encoded.reverse().toString();
    }
}
