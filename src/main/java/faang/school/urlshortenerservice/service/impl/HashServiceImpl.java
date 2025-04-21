package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final HashRepository hashRepository;

    @Override
    public void saveHashBatch(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }
}
