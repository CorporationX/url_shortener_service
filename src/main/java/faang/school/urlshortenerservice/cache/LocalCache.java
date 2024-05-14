package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashGenerator hashGenerator;

    @Async
    public void gen() {
        if (hash<20 %){
            hashGenerator.generate();
        }
        //Quye<>
    }
}
