package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers){
        return numbers.stream()
                .map(this::generateHash)
                .collect(Collectors.toList());
    }

    private String generateHash(Long number){
        StringBuilder sb = new StringBuilder();
        while (number > 0){
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARACTERS.charAt(remainder));
            number = number / 62;
        }
        return sb.toString();
    }
}
