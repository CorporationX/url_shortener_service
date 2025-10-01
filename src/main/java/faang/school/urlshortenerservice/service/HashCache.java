package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.context.HashProperty;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private CopyOnWriteArraySet<String> hashSet = new CopyOnWriteArraySet<>();
    private final HashRepository hashRepository;
    private final HashProperty hashProperty;

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static Thread exclusiveThread = null;

    @PostConstruct
    public void init() {
        List<Hash> content = hashRepository.findAll(PageRequest.of(0, hashProperty.maxHashLength())).getContent();
        if (content.isEmpty() || content.size() < hashProperty.maxHashLength()) {
            hashGenerator.generateBatch();
        } else {
            content.forEach(hash -> hashSet.add(hash.getHash()));
        }
    }

    public void add(String value) {
        hashSet.add(value);
    }

    public String get() {
        Optional<String> any = hashSet.stream()
                .findAny();
        if (any.isPresent()) {
            hashSet.remove(any.get());
            hashRepository.deleteById(any.get());
            if (hashSet.size() < hashProperty.maxHashLength() * hashProperty.minHashPercent()) {
                lock.lock();
                try {
                    while (exclusiveThread != null && Thread.currentThread() != exclusiveThread) {
                       condition.await();
                    }
                    hashSet.addAll(hashGenerator.generateBatch().get());
                    log.info("Коллекция пополнена" + hashSet);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
            return any.get();
        } else {
            return null;
        }
    }
}
