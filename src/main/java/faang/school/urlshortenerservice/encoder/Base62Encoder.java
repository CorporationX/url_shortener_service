package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.exception.ExceedHashDigitsLimitException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder implements BaseEncoder {

    @Value("${spring.encoder.base-string}")
    private String base62;
    private int base;
    @Value("${spring.encoder.number-of-digits}")
    private int digits;

    @PostConstruct
    public void init() {
        base = base62.length();
    }

    @Override
    public List<String> batchEncoding(List<Long> numbers) {
        List<String> result = new ArrayList<>();
        for (Long number : numbers) {
            result.add(encode(number));
        }
        return result;
    }

    @Override
    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number != 0) {
            stringBuilder.append(base62.charAt((int)(number % base)));
            number /= base;
        }
        while (stringBuilder.length() < digits) {
            stringBuilder.append(0);
        }
        if (stringBuilder.length() > digits) {
            log.error("Encoded string exceeds digits limit");
            throw new ExceedHashDigitsLimitException("Encoded string exceeds digits limit");
        }
        return stringBuilder.reverse().toString();
    }
}