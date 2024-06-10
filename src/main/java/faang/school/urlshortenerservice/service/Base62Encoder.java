package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder implements BaseEncoder {

    public static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::encode).toList();
    }

    @Override
    public String encode(Long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        } while (number > 0);
        return stringBuilder.toString();
    }
}
