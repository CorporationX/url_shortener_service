package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public List<String> findAllByPackSize(int packSize) {
        List<String> hashes = hashRepository.findAllAndDeleteByPackSize(packSize);

        if (hashes.size() < packSize) {
            throw new RuntimeException("Not enough hashes available, try again later.");
        }
        return hashes;
    }

    @Transactional(readOnly = true)
    public Long getHashesSize() {
        return hashRepository.count();
    }
}