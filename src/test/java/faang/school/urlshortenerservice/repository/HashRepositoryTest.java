package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Test
    @Transactional
    public void testGetUniqueNumbers() {
        long count = 5;
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);

        assertThat(uniqueNumbers).hasSize((int) count);
        assertThat(uniqueNumbers).doesNotHaveDuplicates();
    }

    @Test
    @Transactional
    public void testGetHashBatch() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());

        hashRepository.saveAll(hashEntities);

        int batchSize = 2;
        List<String> retrievedHashes = hashRepository.getHashBatch(batchSize);

        assertThat(retrievedHashes).hasSize(batchSize);

        List<Hash> remainingHashes = (List<Hash>) hashRepository.findAll();
        assertThat(remainingHashes).hasSize(hashes.size() - batchSize);
    }
}
