package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class HashController {

    private final HashGenerator hashGenerator;

    @PostMapping("/hash")
    public void hashGenerate() {
        hashGenerator.generateBatch();
    }
}
