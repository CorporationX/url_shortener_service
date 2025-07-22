package faang.school.urlshortenerservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TmpController {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    @GetMapping("/nextNumBatchOf/{count}")
    public List<Long> getNextNumBatch(@PathVariable int count) {
        List<Long> uniqueNumbers = hashRepository.getNextNumBachOf(count);
        return uniqueNumbers;
    }

    @GetMapping("/nextHashBatchOf/{count}")
    public List<String> getHashBatch(@PathVariable int count) {
        List<String> hashes = hashRepository.getHashBatchOf(count);
        return hashes;
    }

    @GetMapping("/hashText")
    public String getHash() {
        return hashCache.getHash();
    }

    @PostMapping("/nextHashBatchOf/{count}")
    public List<Hash> saveCacheBatch(@PathVariable int count) {
        List<Long> uniqueNumbers = hashRepository.getNextNumBachOf(count);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        List<Hash> generatedHashes = new ArrayList<>();
        for (String hash : hashes) {
            generatedHashes.add(new Hash(hash));
        }
        List<Hash> savedHashes = hashRepository.saveAll(generatedHashes);
        return savedHashes;
    }

    @PostMapping("/generateHashes")
    public void generateHashes() {
        hashGenerator.generateBatch(); 
    }
}
