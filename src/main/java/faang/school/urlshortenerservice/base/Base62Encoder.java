package faang.school.urlshortenerservice.base;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public List<Hash> encode(List<Long> nums) {
        List<Hash> hashes = new ArrayList<>();
        for (Long num : nums) {
            StringBuilder hash = new StringBuilder();

            while (num > 0) {
                long remaining = num % 62;
                num /= 62;
                hash.append(symbols.charAt((int) remaining));
            }
            hashes.add(new Hash(hash.reverse().toString()));
        }
        return hashes;
    }
}
