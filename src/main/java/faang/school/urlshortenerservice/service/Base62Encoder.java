package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> Hash.builder()
                        .id(number)
                        .hash(encodeToBase62(number))
                        .build()
                ).toList();
    }

    public String encodeToBase62(long number) {

        StringBuilder encoded = new StringBuilder();
        do {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        } while (number > 0);

        return encoded.reverse().toString();
    }
}