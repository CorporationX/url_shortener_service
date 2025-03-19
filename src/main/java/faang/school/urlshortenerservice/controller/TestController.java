package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hash")
public class TestController {

    private final HashGenerator hashGenerator;

    @PostMapping("/generate")
    public ResponseEntity<String> generateAndSaveBatch() {
        try {
            hashGenerator.generateBatch();
            return ResponseEntity.ok("Batch generation started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating batch: " + e.getMessage());
        }
    }
}
