package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/testing/hash/repository")
public class HashRepoAndGeneratorTestController {
    private final HashRepository repository;
    private final HashGenerator generator;

    @GetMapping("/unique/{n}")
    public List<Long> getUniqueNums(@PathVariable("n") int n) {
        return repository.getUniqueNumbers(n);
    }

    @PutMapping("/hashes")
    public void putHashes(@RequestBody List<String> hashes) {
        repository.save(hashes);
    }

    @GetMapping("/hashes")
    public List<String> getHashes() {
        return repository.getHashBatch();
    }

    @PutMapping("/generate")
    public void generate() {
        generator.generateBatch();
    }
}
