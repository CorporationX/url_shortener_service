package faang.school.urlshortenerservice.base;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public List<String> encode(List<Long> nums) {
        List<String> hashes = new ArrayList<>();
        for (Long num : nums) {
            StringBuilder hash = new StringBuilder();

            while (num > 0) {
                long remaining = num % 62;
                num /= 62;
                hash.append(symbols.charAt((int) remaining));
            }
            hashes.add(hash.reverse().toString());
        }
        return hashes;
    }
}
