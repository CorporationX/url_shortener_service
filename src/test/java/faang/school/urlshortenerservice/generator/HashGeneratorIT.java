package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.PostgreSQLExtension;
import faang.school.urlshortenerservice.cache.HashCacheInitializer;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Sql(
        scripts = "classpath:sql/cleaner.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@ExtendWith(PostgreSQLExtension.class)
@TestPropertySource(properties = {"hash.range=10000", "hash.processing.batch.divider=100", "hash.processing.pool.size.core=5"})
class HashGeneratorIT {

    @Autowired
    private HashGenerator hashGenerator;
    @Autowired
    private HashRepository hashRepository;

    @MockBean
    private HashCacheInitializer hashCacheInitializer;


    @Test
    void generateHashes_shouldGenerateExpectedHashes() {
        hashGenerator.generateHashes();

        List<String> generatedHashes = hashRepository.findAll().stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
        int expectedSize = 10000;
        assertThat(generatedHashes)
                .hasSize(expectedSize)
                .contains("B")
                .contains("1E");
        System.out.println("ГУД");
    }

}

