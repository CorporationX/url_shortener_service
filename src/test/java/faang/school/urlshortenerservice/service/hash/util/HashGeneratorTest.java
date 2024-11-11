package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.encode.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static faang.school.urlshortenerservice.test.utils.TestData.NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    private static final int NUMBER_SIZE = 2_000;

    @Mock
    private HashService hashService;

    @Mock
    private Base62Encoder encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "numberSize", NUMBER_SIZE);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGenerate_successful() {
        when(hashService.getUniqueNumbers(NUMBER_SIZE)).thenReturn(NUMBERS);
        when(encoder.encode(NUMBERS)).thenReturn(HASHES);

        hashGenerator.generate();

        ArgumentCaptor<List<String>> hashesCaptor = ArgumentCaptor.forClass(List.class);

        verify(hashService).saveAllBatch(hashesCaptor.capture());
        assertThat(hashesCaptor.getValue()).isEqualTo(HASHES);
    }
}