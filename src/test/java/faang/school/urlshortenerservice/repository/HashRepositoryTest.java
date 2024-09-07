package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Test
    void testGetUniqueNumbers() {
        List<Long> result = hashRepository.getUniqueNumbers(5);
        Set<Long> resultAsSet = new HashSet<>(result);

        assertThat(resultAsSet).hasSize(5);
    }

    @Test
    void testGetHashBatch() {
        hashRepository.saveAll(List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3")));

        List<String> deletedHashes = hashRepository.getHashBatch(3);
        assertThat(deletedHashes).hasSize(3);
        assertThat(deletedHashes).contains("hash1", "hash2", "hash3");

        List<Hash> allHashes = hashRepository.findAll();
        assertThat(allHashes).isEmpty();
    }
}