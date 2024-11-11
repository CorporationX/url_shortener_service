package faang.school.urlshortenerservice.controller;

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
public class HashRepoTestController {
    private final HashRepository repository;
    @GetMapping("/unique/{n}")
    public List<Integer> getUniqueNums(@PathVariable("n") int n) {
        return repository.getUniqueNumbers(n);
    }

    @PutMapping("/hashes")
    public void putHashes(@RequestBody List<String> hashes) {
        repository.save(hashes);
    }
}
