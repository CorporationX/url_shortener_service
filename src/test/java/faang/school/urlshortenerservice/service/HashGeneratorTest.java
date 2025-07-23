package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    // Подменяем поля Value аннотаций статическими значениями
    private static final Long MAX_AMOUNT = 10L;
    private static final Integer MIN_PERCENT = 50;
    private static final Integer COEFF = 1;

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        // Используем рефлексию для замены private свойств
        Field[] fields = HashGenerator.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            switch (field.getName()) {
                case "maxAmount":
                    field.set(hashGenerator, MAX_AMOUNT);
                    break;
                case "minPercent":
                    field.set(hashGenerator, MIN_PERCENT);
                    break;
                case "coeff":
                    field.set(hashGenerator, COEFF);
                    break;
                default:
                    field.setAccessible(false);
            }
        }
    }

    @Test
    void testGetHashesWhenHasHashesInRepo() {
        long range = 5L;
        List<String> mockHashes = new ArrayList<>();
        mockHashes.add("test-hash");
        when(hashRepository.getPortionOfHashes(anyLong())).thenReturn(mockHashes);

        List<String> result = hashGenerator.getHashes(range);

        assertThat(result)
            .containsExactlyElementsOf(mockHashes);
    }

    @Test
    void testGetHashesWithGeneration() {
        long range = 5L;
        List<String> emptyList = Collections.emptyList();
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getPortionOfHashes(anyLong()))
            .thenReturn(emptyList)
            .thenReturn(List.of("a", "b", "c", "d", "e", "f"));
        when(hashRepository.getUniqueNumbers(range)).thenReturn(numbers);
        when(hashRepository.saveAll(any())).thenReturn(any());

        List<String> result = hashGenerator.getHashes(range);

        assertEquals(6, result.size());
        verify(base62Encoder).encode(any());
        verify(hashRepository).saveAll(any());
    }

    @Test
    void testGenerateBatchByScheduleWhenHashIsFull() {
        long range = 5L;
        when(hashRepository.countAll()).thenReturn(MAX_AMOUNT);

        hashGenerator.generateBatchBySchedule(range);

        verify(hashRepository, times(0)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(0)).encode(any());
        verify(hashRepository, times(0)).saveAll(any());
    }
}