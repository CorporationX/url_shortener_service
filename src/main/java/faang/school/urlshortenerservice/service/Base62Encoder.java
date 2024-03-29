package faang.school.urlshortenerservice.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        List<String> listEncode = new ArrayList<>();
        for (long number : numbers) {
            StringBuilder builder = new StringBuilder();
            if (number == 0) {
                builder.append('0');
            } else {
                while (number > 0) {
                    builder.append(BASE62.charAt((int) number % BASE62.length()));
                    number = (int) number / BASE62.length();
                }
            }
            listEncode.add(builder.toString());
        }
        return listEncode;
    }
}
