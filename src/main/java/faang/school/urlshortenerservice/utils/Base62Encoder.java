package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import java.util.List;

public class Base62Encoder {
    private static final String CHARACTERS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARACTERS.length();

    public static List<Hash> encodeBatch(List<Long> numbers) {
        return numbers.stream()
            .map(Base62Encoder::encode)
            .toList();
    }

    private static Hash encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int index = (int) (number % BASE);
            sb.append(CHARACTERS.charAt(index));
            number /= BASE;
        }
        return new Hash(sb.toString());
    }
}
