package faang.school.urlshortenerservice.service;

import java.util.List;

public abstract class  BaseEncoder {

    private final int base;
    private final String characters;

    protected BaseEncoder(int base, String characters) {
        this.base = base;
        this.characters = characters;
    }

    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }

    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map((number) -> encode(number))
                .toList();
    }
}