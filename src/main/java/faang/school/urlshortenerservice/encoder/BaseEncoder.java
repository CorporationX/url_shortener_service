package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BaseEncoder {

    @Value("${encode-base.alphabet}")
    private String alphabet;

    @Value("${encode-base.base:62}")
    private int base;

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase)
                .toList();
    }

    private Hash encodeBase(long number) {
        if (number <= 0) {
            log.error("Число для кодировки хэша должно быть больше 0, переданное число: {}", number);
            throw new NumberFormatException("Число для кодировки хэша должно быть больше 0");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(alphabet.charAt((int) (number % base)));
            number /= base;
        }

        return Hash.builder()
                .hash(stringBuilder.reverse().toString())
                .build();
    }
}
