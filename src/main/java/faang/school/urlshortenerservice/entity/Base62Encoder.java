package faang.school.urlshortenerservice.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGHTBASE62 = BASE_62_CHARACTERS.length();

    public List<String> applyBase62Encoding(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            StringBuilder builder = new StringBuilder();
            while (number > 0) {
                builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
                number /= LENGHTBASE62;
            }
            hashes.add(builder.toString());

        }
        return hashes;
    }
}