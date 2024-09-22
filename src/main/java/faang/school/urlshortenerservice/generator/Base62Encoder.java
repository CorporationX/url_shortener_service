package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long number){
        StringBuilder builder = new StringBuilder();
        while(number > 0){
            builder.append(BASE.charAt((int) number % BASE.length()));
            number /= BASE.length();
        }
        return builder.toString();
    }

    public List<Hash> encodeList(List<Long> numbers){
        return numbers.stream()
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

}
