package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashFreeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HashFreeServiceTest {
    @InjectMocks
    public HashFreeService hashFreeService;

    @Mock
    public HashFreeRepository hashFreeRepository;

    @Mock
    private EntityManager entityManager;

    private int getBatchSize;

    @BeforeEach
    public void setUp() {
        getBatchSize = 3;

        hashFreeService.setGetBatchSize(3);
    }

    @Test
    public void getHashBatchTest() {
        hashFreeService.getHashBatch();

        verify(hashFreeRepository, times(1)).getRandomHashFree(getBatchSize);
    }
}
