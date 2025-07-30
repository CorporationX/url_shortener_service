package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface HashService {

    List<Hash> saveAllHashes(List<Hash> hashes);

    List<String> getFreeHashes();

    void refillHashAsync(Runnable runnable, String storageType, AtomicBoolean block);
}
