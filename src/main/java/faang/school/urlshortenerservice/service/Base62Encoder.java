package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (long number : numbers) {
            builder.setLength(0);
            while (number > 0) {
                builder.append(CHARACTERS.charAt((int) (number % CHARACTERS.length())));
                number /= CHARACTERS.length();
            }
            hashes.add(new Hash(builder.toString()));
        }
        return hashes;
    }
}
