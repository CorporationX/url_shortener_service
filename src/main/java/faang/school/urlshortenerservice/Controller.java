package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class Controller {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;


    @GetMapping()
    public void getSeq() {
        hashGenerator.generateBatch();

    }

}
