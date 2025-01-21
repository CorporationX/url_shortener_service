package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class BaseEncoder {

    @Value("${encoder-values.characters}")
    private String characters;

    public List<Hash> encodeList(List<Long> numbers) {
        return numbers.parallelStream()
                .map(this::encode)
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
    }

    private String encode(Long number) {
        StringBuilder hashBuilder = new StringBuilder();
        int base = characters.length();

        while (number > 0) {
            hashBuilder.append(characters.charAt((int) (number % base)));
            number /= base;
        }

        return hashBuilder.toString();
    }
}
