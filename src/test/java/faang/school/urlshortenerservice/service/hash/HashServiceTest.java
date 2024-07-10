package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {
    @InjectMocks
    private HashService hashService;

    @Mock
    private EntityManager entityManager;

    private int batchSize = 2;

    @BeforeEach
    public void setUp() {
        hashService.setBatchSize(2);
    }

    @Test
    public void testSaveHashes() {
        List<String> hashList = List.of("hash1", "hash2", "hash3", "hash4");

        hashService.saveHashes(hashList);

        verify(entityManager, times(4)).persist(any(Hash.class));
        verify(entityManager, times(3)).flush();
        verify(entityManager, times(3)).clear();
    }
}
