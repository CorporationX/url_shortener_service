package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE_62_LENGTH = BASE_62_CHARACTERS.length();

    public List<String> encode(List<Long> range) {
        List<String> hashList = new ArrayList<>();

        range.forEach(number -> {
            StringBuilder stringBuilder = new StringBuilder();
            while (number > 0) {
                int rem = (int) (number % BASE_62_LENGTH);
                stringBuilder.append(BASE_62_CHARACTERS.charAt(rem));
                number /= BASE_62_LENGTH;
            }
            hashList.add(stringBuilder.toString());
        });

        return hashList;
    }
}
