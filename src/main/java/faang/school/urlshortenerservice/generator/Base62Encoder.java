package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Base62Encoder {

    private final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<Hash> encode(List<Long> numbers) {
        log.info("Hash generation occurs");
        return numbers.parallelStream()
                .map(this::applyBase62)
                .map(Hash::new)
                .toList();
    }

    private String applyBase62(long number) {
        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            hash.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return hash.toString();
    }

}
