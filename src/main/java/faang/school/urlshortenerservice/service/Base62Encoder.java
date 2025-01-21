package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int BASE = CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::toEncode).toList();
    }

    private String toEncode(Long number){
        StringBuilder sb = new StringBuilder();
        do {
            long index = number % BASE;
            number /= BASE;
            sb.append(CHARACTERS.charAt((int) index));
        } while(number > 0);
        return sb.toString();
    }
}
