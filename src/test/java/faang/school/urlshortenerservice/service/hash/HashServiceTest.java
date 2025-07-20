package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashMapper hashMapper;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashService hashService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashService, "batchSize", 10); // для генерации
    }

    @Test
    void getHashes_shouldReturnFromDatabase() {
        List<Hash> hashes = List.of(new Hash("abc"), new Hash("def"));
        when(hashRepository.getAndDeleteHashBatch(2L)).thenReturn(hashes);

        CompletableFuture<List<Hash>> result = hashService.getHashes(2);

        assertEquals(hashes, result.join());
        verify(hashRepository).getAndDeleteHashBatch(2L);
        verifyNoInteractions(hashGenerator);
    }

    @Test
    void generateHashes_shouldGenerateAndSave() {
        List<Long> uniqueNumbers = List.of(100L, 101L);
        List<String> encoded = List.of("ab", "cd");
        List<Hash> hashEntities = List.of(new Hash("ab"), new Hash("cd"));

        when(hashRepository.getNextUniqueNumbers(2)).thenReturn(uniqueNumbers);
        when(hashGenerator.generateHashBatch(uniqueNumbers))
                .thenReturn(CompletableFuture.completedFuture(encoded));
        when(hashMapper.toEntity(encoded)).thenReturn(hashEntities);

        hashService.generateHashes(2);

        verify(hashRepository).saveAll(hashEntities);
    }
}