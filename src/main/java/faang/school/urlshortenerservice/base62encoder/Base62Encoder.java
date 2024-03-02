package faang.school.urlshortenerservice.base62encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public List<String> encodeList(List<Long> numbers) {
        return numbers.stream().map(this::encoder).toList();
    }

    public String encoder(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while(number > 0) {
            stringBuilder.append(BASE_62_CHARACTERS.charAt((int) number%62));
            number /= 62;
        }
        return String.valueOf(stringBuilder);
    }

}
