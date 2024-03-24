package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62EncoderImpl implements Base62Encoder {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final int alphabetSize = ALPHABET.length();

    @Override
    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int i = (int) (number % alphabetSize);
            sb.append(ALPHABET.charAt(i));
            number /= alphabetSize;
        }
        return sb.reverse().toString();
    }

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

}