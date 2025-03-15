package faang.school.urlshortenerservice.service.hash.impl;

import faang.school.urlshortenerservice.exception.HashNotExistException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashCacheAbstract;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class HashCache extends HashCacheAbstract {
    private static final String MESSAGE = "Queue is empty, waiting for hash to be cached...";

    public HashCache(UrlShortenerProperties properties,
                     ExecutorService executorService,
                     HashService hashService,
                     HashGenerator hashGenerator) {
        super(new ConcurrentLinkedQueue<>(), properties, executorService, hashService, hashGenerator);
    }

    public Hash getHashAndGenerate() {
        Hash poll = queue.poll();
        if (poll == null) {
            log.info(MESSAGE);
            throw new HashNotExistException(MESSAGE);
        }
        return poll;
    }
}
