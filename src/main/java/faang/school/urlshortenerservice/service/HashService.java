package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;

    public void save(List<String> hashes) {
        hashRepository.save(hashes);
    }

    @Transactional
    public List<Long> getUniqueNumbers() {
        return hashRepository.getUniqueNumbers();
    }

    public List<String> getHashBatch() {
        return hashRepository.getHashBatch();
    }

    public Long getCount() {
        return hashRepository.getCount();
    }


}
