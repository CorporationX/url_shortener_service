package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    public static final String  BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String base62Encode(long number) {
        int baseLength = BASE_62_CHARACTERS.length();
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE_62_CHARACTERS.charAt((int) (number % baseLength)));
            number /= baseLength;
        }
        return stringBuilder.toString();
    }

    public List<String> base62EncodeList(List<Long> list) {
         return list.stream()
                .map(this::base62Encode)
                .toList();
    }
}
