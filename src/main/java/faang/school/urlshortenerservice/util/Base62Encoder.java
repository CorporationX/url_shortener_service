package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.stereotype.Component;
import org.unbrokendome.base62.Base62;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String FIRST_WORD_WITH_SIX_SIGNS = "00000100000";
    private static final String LAST_WORD_WITH_SIX_SIGNS = "00000zzzzzz";

    public Base62Encoder(HashRepository hashRepository) {
        long[] minValue = Base62.decodeArray(FIRST_WORD_WITH_SIX_SIGNS);
        long[] maxValue = Base62.decodeArray(LAST_WORD_WITH_SIX_SIGNS);

        Long firstNumber = hashRepository.getNextUniqueNumber();

        if (firstNumber < minValue[0] || firstNumber > maxValue[0]) {
            throw new IllegalStateException(
                    String.format("Application failed to start." +
                                    " First number %d from unique_number_seq is out of allowed range [%d, %d]",
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
