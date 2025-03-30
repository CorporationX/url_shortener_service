package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FreeHashGenerator {
    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE_62_LENGTH = 62;

    public List<FreeHash> generateHashes(List<Long> range) {
        log.info("generating {} new hashes...", range.size());

        return range.stream()
                .map(this::applyBase62Encoding)
                .map(FreeHash::new)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_LENGTH)));
            number /= BASE_62_LENGTH;
        }
        return builder.toString();
    }
}
