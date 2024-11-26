package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;

    @Override
    public List<Long> getUniqueNumbers(int number) {
        return hashRepository.getUniqueNumbers(number);
    }

    @Override
    @Transactional
    public void saveBatch(List<String> hashes) {
        hashRepository.saveBatch(hashes);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashes(int number) {
        List<String> hashes = hashRepository.getHashBatch(number);
        return CompletableFuture.completedFuture(hashes);
    }
}