package faang.school.urlshortenerservice.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HashEncoder {

    public String encodeBase62(long num) {
        final String b62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        log.debug("Generating hash value for ID {}", num);
        while (num > 0) {
            sb.append(b62Chars.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
