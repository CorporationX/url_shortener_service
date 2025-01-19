package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGTH = BASE62.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashList = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(BASE62.charAt((int) (number % BASE_LENGTH)));
                number /= BASE_LENGTH;
            } while (number > 0);
            hashList.add(builder.reverse().toString());
        }
        return hashList;
    }
}
