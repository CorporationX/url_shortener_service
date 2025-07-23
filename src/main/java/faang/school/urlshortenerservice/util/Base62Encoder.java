package faang.school.urlshortenerservice.util;

import io.netty.handler.codec.base64.Base64Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder extends Base64Encoder {
    private static final String CHAR_ARRAY = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ARRAY_LEN = CHAR_ARRAY.length();
    private static final int MAX_HASH_LENGTH = 7;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
            .map(this::encode)
            .filter(hash -> hash.length() <= MAX_HASH_LENGTH)
            .toList();
    }

    private String encode(Long number) {
        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % ARRAY_LEN);
            hash.insert(0, CHAR_ARRAY.charAt(remainder));
            number /= ARRAY_LEN;
        }
        if (hash.length() > MAX_HASH_LENGTH) {
            log.warn("Hash length exceeded");
        }
        return hash.toString();
    }
}
