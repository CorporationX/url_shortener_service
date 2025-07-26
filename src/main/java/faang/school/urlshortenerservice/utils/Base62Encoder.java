package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    /**
     * Кодирует число типа long в строку Base62 заданной длины.
     * Если сгенерированная строка короче requiredLength, она дополняется нулями спереди.
     *
     * @param num Число для кодирования.
     * @param requiredLength Требуемая длина выходной строки.
     * @return Строка Base62 заданной длины.
     */
    public String encode(long num, int requiredLength) {
        if (num < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        if (requiredLength <= 0) {
            throw new IllegalArgumentException("Required length must be positive.");
        }

        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            sb.append(ALPHABET.charAt(0)); // Для числа 0
        } else {
            while (num > 0) {
                sb.append(ALPHABET.charAt((int) (num % BASE)));
                num /= BASE;
            }
        }
        // Переворачиваем строку, так как она строилась в обратном порядке
        String encoded = sb.reverse().toString();

        // Дополняем нулями спереди, если строка короче требуемой длины
        while (encoded.length() < requiredLength) {
            encoded = ALPHABET.charAt(0) + encoded;
        }

        // Обрезаем, если строка длиннее требуемой (хотя при правильном использовании не должно быть)
        if (encoded.length() > requiredLength) {
            // Это может произойти, если num слишком велико для requiredLength
            // В реальном приложении нужно продумать стратегию: выбросить исключение, использовать более длинный хеш и т.д.
            // Для 6 символов и long, это маловероятно, если num не превышает 62^6
            throw new IllegalArgumentException("Generated hash is too long for required length " + requiredLength);
        }

        return encoded;
    }
}