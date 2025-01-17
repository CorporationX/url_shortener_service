package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
public class BaseEncoder {

    @Value("${encoder-values.characters}")
    private String characters;

    public List<Hash> encodeList(List<Long> numbers) {
        return  numbers.parallelStream()
                .map(this::encode)
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
    }

    private String encode(Long number) {
        var sb = new StringBuilder();
        int base = characters.length();

        while (number > 0) {
            sb.append(characters.charAt((int) (number % base)));
            number /= base;
        }

        return sb.toString();
    }
}
