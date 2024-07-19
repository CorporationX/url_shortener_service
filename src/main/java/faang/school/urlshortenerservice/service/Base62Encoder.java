package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE_62_CHARACTERS.length();

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .map(Hash::new)
                .collect(Collectors.toList());
    }

    private String encodeBase62(Long number) {
        Random random = new Random();
        StringBuilder hash = new StringBuilder();

        while (number > 0) {
            int digit = (int) (number % BASE);
            hash.append(BASE_62_CHARACTERS.charAt(digit));
            number /= BASE;
        }
        while (hash.length() < 6) {
            hash.append(BASE_62_CHARACTERS.charAt(random.nextInt(BASE)));
        }

        return hash.toString();
    }
}
