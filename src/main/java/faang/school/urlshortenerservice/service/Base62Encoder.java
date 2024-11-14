package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<Hash> encode(List<Long> numbers) {
        log.info("start encode with number - {}", numbers.get(0));

        List<Hash> hashes = numbers.stream()
                .map(this::encodeNumber)
                .map(Hash::new)
                .toList();

        log.info("finish encode with number - {}", numbers.get(numbers.size() - 1));
        return hashes;
    }

    private String encodeNumber(long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }

        return builder.toString();
    }
}
