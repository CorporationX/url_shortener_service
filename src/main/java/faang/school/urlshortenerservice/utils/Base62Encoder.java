package faang.school.urlshortenerservice.utils;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(@NotNull List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    if (number == 0) {
                        return String.valueOf(BASE62.charAt(0));
                    }

                    StringBuilder result = new StringBuilder();
                    while (number > 0) {
                        int remainder = (int) (number % 62);
                        result.append(BASE62.charAt(remainder));
                        number /= 62;
                    }
                    return result.reverse().toString();
                })
                .toList();
    }
}
