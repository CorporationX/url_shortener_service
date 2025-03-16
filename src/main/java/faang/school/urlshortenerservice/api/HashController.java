package faang.school.urlshortenerservice.api;

import faang.school.urlshortenerservice.dto.InsertDataDto;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class HashController {
    private final HashRepository hashRepository;
    private final HashService hashService;
    private final HashGenerator hashGenerator;

    @GetMapping("/test")
    public List<Long> test() {
        return hashRepository.getUniqueNumbers(100);
    }

    @PostMapping
    public void saveHashes(@RequestBody @Valid List<@NonNull String> hashes) {
        hashService.saveHashes(hashes);
    }

    @PostMapping("/generator")
    public List<String> hashGenerate() throws ExecutionException, InterruptedException {
        return hashGenerator.generateBatch().get();
    }
}
