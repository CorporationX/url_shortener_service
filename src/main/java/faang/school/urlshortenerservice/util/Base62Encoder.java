package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

@Component
public class Base62Encoder implements LongConsumer {

    private final CharBuffer charBuffer = CharBuffer.allocate(1);
    private final StringBuilder builder = new StringBuilder();
    private static final char[] BASE_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>();
        for(long num : numbers) {
            accept(num);
            Hash hash = Hash.builder()
                    .hash(toString())
                    .build();
            hashes.add(hash);
        }
        return hashes;
    }

    @Override
    public void accept(long value) {
        if (value < 0) {
            for (int i = 10; i > 0; i--) {
                putDigits(i, (int) (-(value % 62)));
                value /= 62;
            }
            putDigits(0, (int) (-(value - 31)));

        } else {
            for (int i = 10; i > 0; i--) {
                putDigits(i, (int) (value % 62));
                value /= 62;
            }
            putDigits(0, (int) value);
        }
        builder.append(charBuffer);
        charBuffer.clear();
    }

    public void putDigits(int index, int charIndex) {
        charBuffer.put(index, getDigits(charIndex));

    }

    public char getDigits(int index) {
        return BASE_DIGITS[index];
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}