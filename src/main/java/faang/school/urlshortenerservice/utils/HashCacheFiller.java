package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCacheFiller {

    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final HashGenerator hashGenerator;

    private long indexOfPreparedHashes;

    @PostConstruct
    protected void init() {
        indexOfPreparedHashes = preparedUrlHashRepository.count();
        triggerCacheFilling();
    }

    @Async("taskExecutor")
    public void triggerRefill() {
        triggerCacheFilling();
    }

    private void triggerCacheFilling() {
        Long newIndex = hashGenerator.generateHashes(indexOfPreparedHashes);
        if (newIndex != null) indexOfPreparedHashes = newIndex;
    }
}