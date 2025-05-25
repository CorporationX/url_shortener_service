package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Base62Encoder {

    private static final String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            log.error("Список не может быть пуст");
            throw new DataValidationException("Необходимо хотя бы одно число");
        }
        List<String> result = numbers.stream()
                .map(number -> {
                    StringBuilder builder = new StringBuilder();
                    while (number > 0) {
                        builder.append(base62.charAt((int) (number % base62.length())));
                        number /= base62.length();
                    }
                    return  builder.toString();
                })
                .toList();
        log.debug("Получено {} уникальных хеша", numbers.size());
        return result;
    }

}
