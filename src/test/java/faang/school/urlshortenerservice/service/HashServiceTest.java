package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.urlshortenerservice.entity.HashBuilder.build;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    private static final String HASH_1 = "qwerty";
    private static final String HASH_2 = "abc123";

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void testSaveAll_Success() {
        List<Hash> hashes = List.of(build(HASH_1), build(HASH_2));

        when(hashRepository.saveAll(anyList())).thenReturn(hashes);

        hashService.saveAll(List.of(HASH_1, HASH_2));

        verify(hashRepository).saveAll(anyList());
    }
}