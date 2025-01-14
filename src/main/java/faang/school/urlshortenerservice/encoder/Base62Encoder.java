package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> list) {
        return list.parallelStream()
                .map(this::applyBase62Encoding)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE_62_CHARACTER.charAt((int) (number % BASE_62_CHARACTER.length())));
            number /= BASE_62_CHARACTER.length();
        }
        return result.toString();
    }

}
