package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@EnableAsync
class HashGeneratorServiceImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder encoder;

    @InjectMocks
    private HashGeneratorServiceImpl hashGeneratorService;

    private int amountHash;

    @BeforeEach
    void setUp() {
        amountHash = 3;
        ReflectionTestUtils.setField(hashGeneratorService, "amountHash", amountHash);
    }

    @Test
    public void testGenerateBatch_success() throws Exception {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<Hash> encodedHashes = Arrays.asList(
                Hash.builder().hash("000001").build(),
                Hash.builder().hash("000002").build(),
                Hash.builder().hash("000003").build()
        );

        lenient().when(hashRepository.getUniqueNumbers(amountHash)).thenReturn(uniqueNumbers);
        lenient().when(encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);
        when(hashRepository.saveAll(encodedHashes)).thenReturn(encodedHashes);
        CompletableFuture<List<Hash>> future = hashGeneratorService.generateBatch();

        List<Hash> result = future.get();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("000001", result.get(0).getHash());
        assertEquals("000002", result.get(1).getHash());
        assertEquals("000003", result.get(2).getHash());

        verify(hashRepository).getUniqueNumbers(amountHash);
        verify(encoder).encode(uniqueNumbers);
        verify(hashRepository).saveAll(encodedHashes);
    }

    @Test
    public void testGenerateBatch_failure() throws Exception {
        when(hashRepository.getUniqueNumbers(3)).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<List<Hash>> future = hashGeneratorService.generateBatch();

        assertThrows(Exception.class, future::get);
    }
}