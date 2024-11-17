package faang.school.urlshortenerservice.utility.base62;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Base62Encoder {
    private static final String BASE62_SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> nums) {
        List<String> hashes = new ArrayList<>();
        for (Long num : nums) {
            StringBuilder hash = new StringBuilder();

            while (num > 0) {
                long remaining = num % 62;
                num /= 62;
                hash.append(BASE62_SYMBOLS.charAt((int) remaining));
            }
            hashes.add(hash.reverse().toString());
        }
        return hashes;
    }
}
