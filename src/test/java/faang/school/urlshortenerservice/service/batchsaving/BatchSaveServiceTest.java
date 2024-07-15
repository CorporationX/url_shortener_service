package faang.school.urlshortenerservice.service.batchsaving;

import faang.school.urlshortenerservice.entity.hash.Hash;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BatchSaveServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BatchSaveService batchSaveService;

    @BeforeEach
    public void setUp() {
        batchSaveService.setBatchSize(2);
    }

    @Test
    public void testSaveEntities() {
        List<String> dataList = Arrays.asList("hash1", "hash2", "hash3", "hash4");

        batchSaveService.saveEntities(dataList, Hash.class);

        verify(entityManager, times(4)).persist(any(Hash.class));

        verify(entityManager, times(3)).flush();
        verify(entityManager, times(3)).clear();
    }
}
