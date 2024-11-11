package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE_62_CHARACTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Value("${hash.range:10000}")
    private int maxRange;

    private final HashRepository hashRepository;

    @Transactional
    //TODO по идее лучше отдельно сделать шедулер, чтобы отдельно это конфигурировать,
    // но если в простом варианте, то можно обойтись аннотацией @Scheduled
    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void generateHash() {
        List<Long> range = hashRepository.getNextRange(maxRange);
        //TODO посмотреть show sql true
        List<Hash> hashes = range.stream()
                .map(number -> new Hash(applyBase62Encoding(number)))
                .toList();
        hashRepository.saveAll(hashes);
    }

    private String applyBase62Encoding(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE_62_CHARACTER.charAt((int) (number % BASE_62_CHARACTER.length())));
            number /= BASE_62_CHARACTER.length();
        }
        return stringBuilder.toString();
    }
}
