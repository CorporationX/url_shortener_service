package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashFreeRepository;
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

    private int bathSize;

    @BeforeEach
    public void setUp() {
        bathSize = 3;
        hashFreeService.setBathSize(3);
    }

    @Test
    public void getHashBatchTest() {
        hashFreeService.getHashBatch();

        verify(hashFreeRepository, times(1)).getRandomHashFree(bathSize);
    }
}
