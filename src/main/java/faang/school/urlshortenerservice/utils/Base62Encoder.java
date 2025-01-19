package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE_LENGTH = BASE62.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashList = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(BASE62.charAt((int) (number % BASE_LENGTH)));
                number /= BASE_LENGTH;
            } while (number > 0);
            hashList.add(builder.toString());
        }
        return hashList;
    }
}