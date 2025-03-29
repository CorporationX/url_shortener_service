package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62EncoderImpl implements Base62Encoder {

    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ENCODING_BASE_62 = 62;

    @Override
    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> Hash.builder()
                        .id(number)
                        .hash(encodeToBase62(number))
                        .build()
                ).toList();
    }

    private String encodeToBase62(long number) {
        StringBuilder encoded = new StringBuilder();
        do {
            int remainder = (int) (number % ENCODING_BASE_62);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= ENCODING_BASE_62;
        } while (number > 0);

        return encoded.reverse().toString();
    }
}