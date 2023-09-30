package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final HashGenerator hashGenerator;

    @GetMapping
    public void test() {
        hashGenerator.generateBatch(); // для теста
    }
}
