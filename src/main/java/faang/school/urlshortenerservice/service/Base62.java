package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.ArrayList;
import java.util.List;

public class Base62 {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    List<Hash> encode(List<Long> values) {
        List<Hash> hashes = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (long number : values) {
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
