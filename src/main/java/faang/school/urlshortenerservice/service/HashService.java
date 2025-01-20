package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;

    public void saveAll(List<String> hashes) {
        List<Hash> entities = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(entities);
    }
}
