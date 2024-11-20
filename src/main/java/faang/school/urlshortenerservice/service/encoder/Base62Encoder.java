package faang.school.urlshortenerservice.service.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {
    @Value("${encoder.base62.symbols}")
    private String symbols;

    public List<String> encodeSymbolsToHash(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream().map(this::encode).toList();
    }

    private String encode(Long number) {
        int fixedLength = 6;
        StringBuilder stringBuilder = new StringBuilder();

        do {
            int index = (int) (number % symbols.length());
            stringBuilder.insert(0, symbols.charAt(index));
            number /= symbols.length();
        } while (number > 0);

        while (stringBuilder.length() < fixedLength) {
            stringBuilder.insert(0, symbols.charAt(0));
        }

        return stringBuilder.toString();
    }

}