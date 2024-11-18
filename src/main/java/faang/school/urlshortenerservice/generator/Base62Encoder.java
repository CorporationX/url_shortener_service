package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encodeHashes(List<Long> nums) {
        return nums.stream()
                .map(this::encodeToBase62)
                .map(Hash::new)
                .toList();
    }

    public String encodeToBase62(long num) {
        StringBuilder result = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % 62);
            result.append(BASE62.charAt(remainder));
            num /= 62;
        }
        return result.reverse().toString();
    }
}
