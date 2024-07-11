package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String applyBase62Encoding(long number){
        StringBuilder builder = new StringBuilder();
        while(number > 0){
            builder.append(BASE_62_CHARACTERS.charAt((int) number % BASE_62_CHARACTERS.length()));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }

    public List<Hash> applyBase62Encoding(List<Long> numbers){
        return numbers.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
    }
}
