package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;


}
