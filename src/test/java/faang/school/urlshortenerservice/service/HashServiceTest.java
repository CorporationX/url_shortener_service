package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

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
        doNothing().when(hashRepository).saveBatch(anyList());

        hashService.saveAll(List.of(HASH_1, HASH_2));

        verify(hashRepository).saveBatch(anyList());
    }
}