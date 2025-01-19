package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> list) {
        return list.stream()
                .map(this::encodeBase62)
                .toList();
    }

    private Hash encodeBase62(long value) {
        StringBuilder finalString = new StringBuilder();
        while (value != 0) {
            finalString.append(BASE_62_CHARSET.charAt((int) (value % 62)));
            value /= 62;
        }
        return new Hash(finalString.toString());
    }
}
