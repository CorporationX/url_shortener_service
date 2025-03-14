package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Base62EncoderService implements BaseEncoderService {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE62_LENGTH = BASE62_CHARS.length();

    @Override
    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        for (long number : numbers) {
            hashes.add(encodeToBase62(number));
        }
        return hashes;
    }

    private String encodeToBase62(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62_CHARS.charAt((int) (number % BASE62_LENGTH)));
            number /= BASE62_LENGTH;
        }
        return sb.reverse().toString();
    }
}