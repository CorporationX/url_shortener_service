package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Base62 encoder for generating short URL hashes. @Component
 * <p>
 * WARNING: MIN_HASH_LENGTH and BASE_62_CHAR are part of the algorithm.
 * Changing these values will break compatibility with existing URLs in the database.
 * Any modification requires full data migration.
 */
@Component
public class Base62Encoder {
    private static final String BASE_62_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MIN_HASH_LENGTH = 6;
    private static final long OFFSET = (long) Math.pow(BASE_62_CHAR.length(), MIN_HASH_LENGTH - 1);

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>();

        for (long number : numbers) {
            long numberWithOffset = number + OFFSET;
            StringBuilder hash = new StringBuilder();

            while (numberWithOffset > 0) {
                int remainder = (int) (numberWithOffset % BASE_62_CHAR.length());
                hash.append(BASE_62_CHAR.charAt(remainder));
                numberWithOffset = numberWithOffset / BASE_62_CHAR.length();
            }

            result.add(hash.reverse().toString());
        }

        return result;
    }
}