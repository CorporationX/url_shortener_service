package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashGenerator hashGenerator;

    public RedirectView getRedirectUrl  (String hash) {
        //search for url in Redis

        //search for url in PostgreSQL Data Base
        return new RedirectView();
    }

    public void generateHashes() {
        // Вызов асинхронного метода
        CompletableFuture<Void> future = hashGenerator.generateBatch();

        // Обработка результата (опционально)
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Error in hash generation: " + ex.getMessage());
            } else {
                System.out.println("Successful hash generation");
            }
        });
    }
}
