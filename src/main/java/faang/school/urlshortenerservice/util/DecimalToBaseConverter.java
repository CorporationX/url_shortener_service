package faang.school.urlshortenerservice.util;

import java.util.HashMap;
import java.util.Map;

public class DecimalToBaseConverter implements BaseEncoder {
    private final int base;
    private final String characters;
    private final Map<Character, Integer> charIndexMap;

    public DecimalToBaseConverter(int base, String characters) {
        if (base != characters.length()) {
            throw new IllegalArgumentException("Character set length must match the specified base");
        }

        this.base = base;
        this.characters = characters;
        this.charIndexMap = new HashMap<>(base);
        for (int i = 0; i < characters.length(); i++) {
            charIndexMap.put(characters.charAt(i), i);
        }
    }

    @Override
    public String encode(long number) {
        int capacity = Math.max(1, (int) (Math.log(number + 1) / Math.log(base)) + 1);
        StringBuilder stringBuilder = new StringBuilder(capacity);

        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);

        return stringBuilder.toString();
    }

    @Override
    public long decode(String number) {
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            char c = number.charAt(i);
            Integer value = charIndexMap.get(c);
            if (value == null) {
                throw new IllegalArgumentException("Invalid character in input: " + c);
            }
            result = result * base + value;
        }

        return result;
    }
}
