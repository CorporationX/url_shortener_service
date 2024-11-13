package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlCleanerAsync {
    private final HashRepository hashRepository;

    @Async("generatorThreadPool")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanUrl(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }
}
