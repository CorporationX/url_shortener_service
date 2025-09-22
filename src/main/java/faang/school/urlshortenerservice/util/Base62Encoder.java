package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String CHAR_SOURCE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encodeBatch(List<Long> baseList) {
        return baseList.stream().map(this::encodeBase62).toList();
    }

    public String encodeBase62(long base) {
        StringBuilder sb = new StringBuilder();
        while (base > 0) {
            sb.append(CHAR_SOURCE.charAt((int) base % 62));
            base /= 62;
        }
        return sb.reverse().toString();
    }
}