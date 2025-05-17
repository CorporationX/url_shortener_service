package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStartupInitializer {

    private final HashGeneratorService hashGeneratorService;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationStart() {
        hashGeneratorService.generateHashes();
    }
}
