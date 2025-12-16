package faang.school.urlshortenerservice.util;


import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private final UrlShortenerConfig urlShortenerConfig;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        if (numbers.isEmpty()) {
            return hashes;
        }
        for (Long number : numbers) {
            hashes.add(encodeNumber(number));
        }
        return hashes;
    }

    private String encodeNumber(Long number) {
        String base62Chars = urlShortenerConfig.getBase62Chars();
        if (number == 0) {
            return "0";
        }

        StringBuilder stringBuilder = new StringBuilder();

        while (number > 0) {
            int remain = (int) (number % base62Chars.length());
            stringBuilder.append(base62Chars.charAt(remain));
            number /= base62Chars.length();
        }

        return stringBuilder.reverse().toString();
    }
}
