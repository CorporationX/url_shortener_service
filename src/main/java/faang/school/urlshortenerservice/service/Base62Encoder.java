package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    private final int base = 62;
    private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder encodedHash = new StringBuilder();
        do {
            encodedHash.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return encodedHash.toString();
    }

}
