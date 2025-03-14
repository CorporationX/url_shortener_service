package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseEncoder {

    @Value("${encode-base.alphabet}")
    private String alphabet;

    @Value("${encode-base.base}")
    private int base;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeBase(number));
        }

        return hashes;
    }

    private String encodeBase(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(alphabet.charAt((int) (number % base)));
            number /= base;
        }

        return builder.reverse().toString();
    }
}
