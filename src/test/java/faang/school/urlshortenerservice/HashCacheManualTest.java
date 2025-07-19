package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repo.HashRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
public class HashCacheManualTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Base62Encoder base62Encoder;

    @Test
    public void testHashCache() {

        HashRepository repository = new HashRepository(jdbcTemplate);

        List<Long> numbers = repository.getUniqueNumbers(100);

        List<String> hashes = base62Encoder.encode(numbers);

        repository.saveAllHashes(hashes);

        String hash = repository.getHashBatch(1).get(0);
        System.out.println("Полученный хэш: " + hash);
    }
}