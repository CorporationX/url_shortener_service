package faang.school.urlshortenerservice.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class BaseEncoder {

    @Value("${encoder-values.characters}")
    private String characters;

    public List<String> encodeList(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> hashes.add(encode(number)));
        return hashes;
    }

    private String encode(long number) {
        var sb = new StringBuilder();
        int base = characters.length();

        while (number > 0) {
            sb.append(characters.charAt((int) (number % base)));
            number /= base;
        }

        return sb.toString();
    }
}
