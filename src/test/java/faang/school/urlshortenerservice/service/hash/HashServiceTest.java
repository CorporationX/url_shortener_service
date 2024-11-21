package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void testSaveAll() {
        List<String> hashes = List.of("1", "2");
        List<Hash> entities = hashes.stream()
                .map(Hash::new)
                .toList();
        when(hashRepository.saveAll(anyList())).thenReturn(entities);
        hashService.saveAll(hashes);
        verify(hashRepository).saveAll(anyList());
    }
}