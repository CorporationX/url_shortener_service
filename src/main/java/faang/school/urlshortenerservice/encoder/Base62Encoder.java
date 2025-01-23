package faang.school.urlshortenerservice.encoder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Компонент для кодирования чисел в Base62.
 * Base62 использует набор символов: 0-9, A-Z, a-z.
 */
@Slf4j
@Component
public class Base62Encoder {

    /**
     * Набор символов для кодирования в Base62.
     */
    private final static String BASE_62_CHARACTERS =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Кодирует список чисел в список строк в формате Base62.
     *
     * @param numbers Список чисел для кодирования.
     * @return Список строк, закодированных в Base62.
     * @throws IllegalArgumentException Если список чисел пуст или содержит отрицательные значения.
     */
    public List<String> encode(@NonNull List<Long> numbers) {
        log.info("Начало кодирования списка чисел в Base62. Количество чисел: {}", numbers.size());

        if (numbers.isEmpty()) {
            log.error("Список чисел для кодирования пуст.");
            throw new IllegalArgumentException("Список чисел не может быть пустым.");
        }

        List<String> hashes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        numbers.forEach(number -> {
            if (number < 0) {
                log.error("Отрицательное число в списке для кодирования: {}", number);
                throw new IllegalArgumentException("Числа для кодирования не могут быть отрицательными.");
            }

            while (number > 0) {
                stringBuilder.append(BASE_62_CHARACTERS
                    .charAt((int) (number % BASE_62_CHARACTERS.length())));
                number /= BASE_62_CHARACTERS.length();
            }
            hashes.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        });

        log.info("Кодирование завершено. Сгенерировано {} хэшей.", hashes.size());
        return hashes;
    }
}
