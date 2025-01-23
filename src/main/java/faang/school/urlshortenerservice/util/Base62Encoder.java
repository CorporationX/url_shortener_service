package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class Base62Encoder {

    @Value("${base62.alphabet}")
    private String base62Alphabet;

    public List<Hash> encode(List<Long> list) {
        return list.stream()
                .map(this::encodeBase62)
                .toList();
    }

    private Hash encodeBase62(long value) {
        StringBuilder finalString = new StringBuilder();
        int base = base62Alphabet.length();
        while (value != 0) {
            finalString.append(base62Alphabet.charAt((int) (value % base)));
            value /= base;
        }
        return new Hash(finalString.toString());
    }
}
