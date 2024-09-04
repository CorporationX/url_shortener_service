package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    @Value("${custom.base62-characters}")
    private String base62Characters;

    public List<String> encode(List<Long> numbers){
        return numbers.stream()
                .map(this::generateHash)
                .collect(Collectors.toList());
    }

    private String generateHash(Long number){
        if (number == 0) {
            return String.valueOf(base62Characters.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0){
            int remainder = (int) (number % base62Characters.length());
            sb.insert(0, base62Characters.charAt(remainder));
            number = number / 62;
        }
        return sb.toString();
    }
}
