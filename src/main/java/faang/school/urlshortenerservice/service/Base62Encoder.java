package faang.school.urlshortenerservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long number)
    {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        if (number == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62_ALPHABET.charAt((int) (number % 62)));
            number /= 62;
        }
        return sb.reverse().toString();
    }

    public List<String> encode(@NotNull List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .collect(Collectors.toList());
    }
}
