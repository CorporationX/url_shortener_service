package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    HashRepository hashRepository;

    @InjectMocks
    HashService hashService;

    @Captor
    ArgumentCaptor<List<Hash>> listArgumentCaptor;

    Integer count = 8;
    Integer limit = 8;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(hashService, "count", count);
        ReflectionTestUtils.setField(hashService, "limit", limit);
    }

    @Test
    void testSave() {
        String hash1 = "hash1";
        String hash2 = "hash2";
        String hash3 = "hash3";
        List<String> hashes = List.of(hash1, hash2, hash3);

        hashService.save(hashes);

        verify(hashRepository).saveAll(listArgumentCaptor.capture());
        assertEquals(hash1, listArgumentCaptor.getValue().get(0).getHash());
        assertEquals(hash2, listArgumentCaptor.getValue().get(1).getHash());
        assertEquals(hash3, listArgumentCaptor.getValue().get(2).getHash());
    }

    @Test
    void testGetUniqueNumbers() {
        hashService.getUniqueNumbers();
        verify(hashRepository).getUniqueNumbers(count);
    }

    @Test
    void testGetAndDeleteHashBatch() {
        hashService.getAndDeleteHashBatch();
        verify(hashRepository).getAndDeleteHashBatch(limit);
    }
}