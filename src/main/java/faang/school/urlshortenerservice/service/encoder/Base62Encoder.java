package faang.school.urlshortenerservice.service.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    @Value("${services.encoder.base62.symbols}")
    private String symbols;

    public List<String> encodeSymbolsToHash(List<Long> hashes) {
        List<String> h = hashes.stream().map(this::encode).toList();
        System.out.println("Hashes:" + h);
        return h;
    }

    private String encode(Long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, symbols.charAt((int) (number % 62)));
            number /= 62;
        } while (number > 0);
        return stringBuilder.toString();
    }
}