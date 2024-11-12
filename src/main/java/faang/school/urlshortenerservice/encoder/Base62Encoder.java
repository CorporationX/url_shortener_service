package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.hash.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<Hash> encode(List<Long> numbers) {
        log.debug("Encoding batch of {} amount", numbers.size());
        return numbers.stream()
                .map(this::encodeNumber)
                .map(Hash::new)
                .collect(Collectors.toList());
    }

    private String encodeNumber(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62.charAt((int) (number % BASE_62.length())));
            number /= BASE_62.length();
        }
        return sb.toString();
    }
}
