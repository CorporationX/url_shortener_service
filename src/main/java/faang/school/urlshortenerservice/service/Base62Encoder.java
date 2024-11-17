package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = CHARACTERS.length();

    @Transactional(readOnly = true)
    public List<Hash> encodeList(List<Long> nums) {
        return nums.stream()
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

    private String encode(long num) {
        if (num == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder encodeNum = new StringBuilder();
        while (num > 0) {
            int index = (int) (num % BASE);
            encodeNum.append(CHARACTERS.charAt(index));
            num /= BASE;
        }
        return encodeNum.reverse().toString();
    }
}
