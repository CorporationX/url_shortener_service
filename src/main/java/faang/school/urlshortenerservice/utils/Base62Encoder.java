package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE_62_LENGTH = BASE_62_CHARACTERS.length();

    public List<Hash> encode(List<Long> numbers) {
         return numbers.stream()
                .map(this::base62Encode)
                .map(Hash::new)
                .toList();
    }

    private String base62Encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % 62)));
            number /= BASE_62_LENGTH;
        }
        return builder.toString();
    }
}
