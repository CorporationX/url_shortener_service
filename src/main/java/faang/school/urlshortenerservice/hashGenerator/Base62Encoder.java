package faang.school.urlshortenerservice.hashGenerator;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    @Setter
    @Value("${hash.base62_charset}")
    private String base62Charset;

    public String encode(long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            int index = (int) (number % base62Charset.length());
            builder.append(base62Charset.charAt(index));

            number /= base62Charset.length();
        }
        return builder.toString();
    }
}
