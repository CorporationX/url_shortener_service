package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.exception.DataValidationException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class BaseEncoder {

    @Value("${encoder-values.base}")
    private Long base;

    @Value("${encoder-values.characters}")
    private String characters;

    public List<String> encodeList(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new DataValidationException("The list of numbers to encode must not be null or empty");
        }

        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> hashes.add(encode(number)));
        return hashes;
    }

    private String encode(long number) {
        var sb = new StringBuilder();

        while (number > 0) {
            sb.append(characters.charAt((int) (number % base)));
            number /= base;
        }

        return sb.toString();
    }
}
