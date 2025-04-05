package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = 62;

    public Base62Encoder() {
        Map<Character, Integer> charIndexMap = new HashMap<>(BASE);
        for (int i = 0; i < BASE62_CHARACTERS.length(); i++) {
            charIndexMap.put(BASE62_CHARACTERS.charAt(i), i);
        }
    }

    public List<String> encode(List<Long> randomNumbersList) {
        UniqueValuesListValidator.validateList(randomNumbersList, "Supplied list of numbers is empty!");
        UniqueValuesListValidator.validateUniqueness(randomNumbersList);

        return randomNumbersList.stream()
                .map(this::encode)
                .collect(Collectors.toList());
    }

    public String encode(long number) {
        int capacity = Math.max(1, (int) (Math.log(number + 1) / Math.log(BASE)) + 1);
        StringBuilder stringBuilder = new StringBuilder(capacity);

        do {
            stringBuilder.insert(0, BASE62_CHARACTERS.charAt((int) (number % BASE)));
            number /= BASE;
        } while (number > 0);

        return stringBuilder.toString();
    }
}