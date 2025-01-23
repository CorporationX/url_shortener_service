package faang.school.urlshortenerservice.service.hash.encoder.impl;

import faang.school.urlshortenerservice.service.hash.encoder.Encoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base64Encoder implements Encoder {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    @Override
    public List<String> encode(List<Long> values, int hashLength) {
        return values.stream().map(value -> getHash(value, hashLength)).toList();
    }

    private String getHash(long value, long hashLength) {
        int base = CHARACTERS.length();
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < hashLength && value > 0; ++i) {
            int remainder = (int) (value % base);
            hash.append(CHARACTERS.charAt(remainder));
            value /= base;
        }
        return hash.toString();
    }
}
