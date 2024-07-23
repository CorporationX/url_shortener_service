package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    /**
     * Возвращает список уникальных хешей длиной 6 симоволов
     *
     * @param numbers список сидов для генерации хешей
     * @return список хешей
     */
    public List<Hash> encodeList(List<Long> numbers) {
        return numbers.stream()
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