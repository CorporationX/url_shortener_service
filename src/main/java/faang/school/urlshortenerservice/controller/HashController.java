package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class HashController {

    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    @PostMapping("/hash")
    public void hashGenerate() {
        hashGenerator.generateBatch();
    }

    @GetMapping("/hash")
    public String getHash() {
        return hashCache.getHash();
    }
}
