package faang.school.urlshortenerservice.HashGenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers) {

        return numbers.stream()
                .map(number -> {
                    StringBuilder builder = new StringBuilder();
                    while (number > 0) {
                        builder.append(base62.charAt((int) (number % base62.length())));
                        number /= base62.length();
                    }
                    return  builder.toString();
                })
                .toList();
    }

}
