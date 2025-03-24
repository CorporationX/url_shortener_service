package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FreeHashGeneratorTest {
    private FreeHashGenerator generator;

    @BeforeEach
    void setUp() {
        Executor executor = Executors.newFixedThreadPool(4);
        generator = new FreeHashGenerator(executor);
        ReflectionTestUtils.setField(generator, "chunkSize", 5);
    }

    @Test
    void generateHashes_ShouldReturnValidHashes() {
        List<Long> range = List.of(1L, 2L, 3L, 999L, 12345L);
        List<FreeHash> hashes = generator.generateHashes(range);

        assertThat(hashes).hasSize(range.size());
        assertThat(hashes).allMatch(hash -> hash.getHash().matches("[0-9A-Za-z]+"));
    }
}