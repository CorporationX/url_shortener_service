package faang.school.urlshortenerservice.service.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    @Value("${base62}")
    private String BASE62;

    public List<String> encodeSymbolsToHash(List<Long> hashes) {
        return hashes.stream().map(this::encode).toList();
    }

    private String encode(Long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, BASE62.charAt((int) (number % 62)));
            number /= 62;
        } while (number > 0);
        return stringBuilder.toString();
    }
}