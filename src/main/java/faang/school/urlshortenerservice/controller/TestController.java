
package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hash")
public class TestController {

    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    @PostMapping("/generate")
    public ResponseEntity<String> generateAndSaveBatch() {
        try {
            hashGenerator.generateBatch();
            return ResponseEntity.ok("Batch generation started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating batch: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<String> getHash() {
        String hash = hashCache.getHash();
        if (hash != null) {
            return ResponseEntity.ok("Retrieved hash: " + hash);
        } else {
            return ResponseEntity.status(404).body("No hashes available in cache.");
        }
    }
}