package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.HashBuilder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public void saveAll(List<String> hashes) {
        List<Hash> entities = hashes.stream()
                .map(HashBuilder::build)
                .toList();
        hashRepository.saveAll(entities);
    }
}
