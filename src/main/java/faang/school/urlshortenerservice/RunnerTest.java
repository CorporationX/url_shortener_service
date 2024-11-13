package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunnerTest implements CommandLineRunner {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${hash.get_batch_size}")
    private long batchSize;

    @Override
    public void run(String... args) throws Exception {


        // TODO asdfsfd
//        hashGenerator.generateBatch();
//        hashGenerator.generateBatch();
//        List<Hash> hashes = hashRepository.getHashBatch(batchSize);
//        hashes.forEach(h -> System.out.println(h.getHash()));
    }
}
