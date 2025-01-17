package faang.school.urlshortenerservice.encoder;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Base62Encoder implements BaseEncoder {

    private final Integer base;
    private final String characters;

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder builder = new StringBuilder(1);
        while (number > 0) {
            builder.append(characters.charAt((int) (number % base)));
            number /= base;
        }
        return builder.toString();
    }
}
