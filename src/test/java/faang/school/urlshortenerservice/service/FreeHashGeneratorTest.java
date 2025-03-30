package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FreeHashGeneratorTest {
    @InjectMocks
    private FreeHashGenerator generator;

    @Test
    void generateHashes_ShouldReturnValidHashes() {
        List<Long> range = List.of(1L, 2L, 3L, 999L, 12345L);
        List<FreeHash> hashes = generator.generateHashes(range);

        assertThat(hashes).hasSize(range.size());
        assertThat(hashes).allMatch(hash -> hash.getHash().matches("[0-9A-Za-z]+"));
    }
}