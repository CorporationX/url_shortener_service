package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HashController {
    private final HashService hashService;

    @GetMapping("/hashes/{maxRange}")
    public List<Long> getHashes(@PathVariable int maxRange) {
        return hashService.getNumbers(maxRange);
    }

    @PostMapping
    public List<Hash> save(@RequestBody List<Hash> hashes) {
        return hashService.save(hashes);
    }

    @GetMapping("/hash/{batchSize}")
    public List<Hash> getHash(@PathVariable int batchSize) {
        return hashService.getAndDelete(batchSize);
    }

    @PostMapping("/generate/{range}")
    public void generate(@PathVariable int range) {
        hashService.generateHash(range);
    }
}
