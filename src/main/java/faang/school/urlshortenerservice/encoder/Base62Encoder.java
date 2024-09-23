package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Base62Encoder {

    @Value("${hash.length}")
    private int hashLength;
    private final char[] BASE_62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public List<String> encode(List<Long> numbers) {

        List<String> hashes = new ArrayList<>();

        for (Long number : numbers) {
            StringBuilder sb = new StringBuilder();

            while (number > 0) {
                sb.insert(0, BASE_62_CHARS[(int) (number % BASE_62_CHARS.length)]);
                number /= BASE_62_CHARS.length;
            }
            String hash = sb.toString();
            if (hash.length() > hashLength) {
                hash = hash.substring(0, hashLength);
            }
            hashes.add(hash);
        }
        log.info("Encoded hashes: {}", hashes);
        return hashes;
    }
}
