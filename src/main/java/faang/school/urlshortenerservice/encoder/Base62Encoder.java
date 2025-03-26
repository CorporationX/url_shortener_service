package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> value) {
        List<String> result = new ArrayList<>();
        for (Long l : value) {
            StringBuilder sb = new StringBuilder();
            while (l > 0) {
                sb.append(BASE62.charAt((int) (l % 62)));
                l /= 62;
            }
            result.add(sb.reverse().toString());
        }
        return result;
    }
}
