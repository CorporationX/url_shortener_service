package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import org.springframework.stereotype.Component;
import org.unbrokendome.base62.Base62;

import java.util.List;

@Component
public class Base62Encoder {
    public Base62Encoder(HashRepository hashRepository) {
        long[] minValue = Base62.decodeArray("00000100000");
        long[] maxValue = Base62.decodeArray("00000zzzzzz");

        Long firstNumber = hashRepository.getNextUniqueNumber();

        if (firstNumber < minValue[0] || firstNumber > maxValue[0]) {
            throw new IllegalStateException(
                    String.format("Application failed to start. First number %d from unique_number_seq is out of allowed range [%d, %d]",
                            firstNumber, minValue[0], maxValue[0]));
        }
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        String encoded = Base62.encode(number);
        return encoded.substring(5, 11);
    }
}
