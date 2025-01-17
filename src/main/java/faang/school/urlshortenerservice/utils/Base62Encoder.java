package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARSET = "4fC8IsoSdr2L79TQiOaek5uNZh1ztmGUvjPwAVpXy3JYclnB0EKqHMxWFDg6Rb";
    private static final int BASE = BASE62_CHARSET.length();

    public List<HashEntity> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .map(hash -> HashEntity.builder().hash(hash).isUsed(false).build())
                .collect(Collectors.toList());
    }

    private String encodeBase62(Long number) {
        if (number == 0) return String.valueOf(BASE62_CHARSET.charAt(0));

        char[] buffer = new char[11];
        int index = buffer.length;

        while (number > 0) {
            buffer[--index] = BASE62_CHARSET.charAt((int) (number % BASE));
            number /= BASE;
        }

        char randomPrefix = BASE62_CHARSET.charAt(ThreadLocalRandom.current().nextInt(BASE));

        return randomPrefix + new String(buffer, index, buffer.length - index);
    }
}
