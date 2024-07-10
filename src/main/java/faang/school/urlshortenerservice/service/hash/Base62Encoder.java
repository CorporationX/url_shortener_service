package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;
    public static final long LOW_BOUND = 916_132_832L;
    public static final long HIGH_BOUND = 56_800_235_583L;


    /**
     * Возвращает список уникальных хешей длиной 6 симоволов
     *
     * @param numbers список сидов для генерации хешей
     * @return список хешей
     */
    public List<Hash> encodeList(List<Long> numbers) {
        return numbers.stream()
                .map(number -> new Random(number).nextLong(LOW_BOUND, HIGH_BOUND))
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

    private String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, CHARACTERS.charAt((int) (number % BASE)));
            number /= BASE;
        } while (number > 0);
        return stringBuilder.toString();
    }
}