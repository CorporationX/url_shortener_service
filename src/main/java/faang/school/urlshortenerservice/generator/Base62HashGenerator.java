package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.UniqueRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Base62HashGenerator implements HashGenerator {

    private final static String BASE_62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final UniqueRangeRepository uniqueRangeRepository;

    public Stream<String> generateHashes(int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        List<Long> uniqueRange = uniqueRangeRepository.getNextUniqueRange(amount);

        Stream<Long> uniqueNumStream = amount <= 1000 ?
                uniqueRange.stream() : uniqueRange.parallelStream();

        return uniqueNumStream
                .map(this::applyBase62Encode);
    }

    private String applyBase62Encode(long num) {
        StringBuilder encoding = new StringBuilder();
        while (num > 0) {
            encoding.append(BASE_62_CHARS.charAt((int) (num % BASE_62_CHARS.length())));
            num /= BASE_62_CHARS.length();
        }
        return encoding.toString();
    }
}
