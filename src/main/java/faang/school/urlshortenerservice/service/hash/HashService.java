package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public void saveAll(List<String> hashes) {
        hashRepository.saveBatch(hashes);
    }
}
