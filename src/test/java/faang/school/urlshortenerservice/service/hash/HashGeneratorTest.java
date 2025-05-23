package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест HashGenerator")
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashConfig hashConfig;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int numberOfElements = 100;

    @BeforeEach
    void setUp() {
        when(hashConfig.getNumberOfElements()).thenReturn(numberOfElements);
    }

    @Test
    @DisplayName("Получает числа, кодирует их и сохраняет")
    public void givenValidNumbers_whenGenerateHashes_thenCallsEncodeAndSave() {
        when(hashRepository.getUniqueNumbers(numberOfElements))
                .thenReturn(List.of(1L, 2L, 3L));
        when(base62Encoder.encodeNumbers(List.of(1L, 2L, 3L)))
                .thenReturn(List.of("a", "b", "c"));

        hashGenerator.generateHashesBatch();

        verify(hashRepository).getUniqueNumbers(100);
        verify(base62Encoder).encodeNumbers(List.of(1L, 2L, 3L));
        verify(hashRepository).saveAll(List.of("a", "b", "c"));
    }
}
