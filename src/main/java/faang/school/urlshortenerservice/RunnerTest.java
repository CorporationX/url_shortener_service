package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunnerTest implements CommandLineRunner {
    private final HashGenerator hashGenerator;

    @Override
    public void run(String... args) throws Exception {

        // TODO asdfsfd
//        hashGenerator.generateBatch();
//        hashGenerator.generateBatch();

    }
}
