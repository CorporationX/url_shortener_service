package faang.school.urlshortenerservice.repository.db;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashRepositoryJpaImplTest {

    @Mock
    private JpaHashRepository jpaHashRepository;

    @InjectMocks
    private HashRepositoryJpaImpl hashRepository;

    @Test
    void getUniqueNumbers() {
        List<Long> numbers = List.of(1L, 2L);
        when(jpaHashRepository.getUniqueNumbers(2)).thenReturn(numbers);

        List<Long> result = hashRepository.getUniqueNumbers(2);

        assertEquals(numbers, result);
        verify(jpaHashRepository, times(1)).getUniqueNumbers(2);
    }

    @Test
    void pollHashBatch() {
        List<String> hashes = List.of("1", "2");
        when(jpaHashRepository.pollHashBatch(2)).thenReturn(hashes);

        List<String> result = hashRepository.pollHashBatch(2);

        assertEquals(hashes, result);
        verify(jpaHashRepository, times(1)).pollHashBatch(2);
    }

    @Test
    void saveBatch() {
        List<String> hashes = List.of("1", "2");
        List<Hash> entities = hashes.stream().map(Hash::new).toList();

        hashRepository.saveBatch(hashes);

        verify(jpaHashRepository, times(1)).saveAll(entities);
    }

    @Test
    void getHashesNumber() {
        int hashesNumber = 2;
        when(jpaHashRepository.getHashesNumber()).thenReturn(hashesNumber);

        int result = hashRepository.getHashesNumber();

        assertEquals(hashesNumber, result);
        verify(jpaHashRepository, times(1)).getHashesNumber();
    }
}