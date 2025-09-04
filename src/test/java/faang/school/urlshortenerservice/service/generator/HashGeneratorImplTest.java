package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.common.encoder.Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorImplTest {
    private static final int QUANTITY = 1;

    @InjectMocks
    private HashGeneratorImpl generator;

    @Mock
    private HashRepository repository;
    @Mock
    private Encoder encoder;


    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(generator, "quantity", QUANTITY);
    }

    @Test
    public void testGenerateBatchFromExistsNumbers() {
        ArrayList<Long> list = new ArrayList<>(List.of(1L));
        ArrayList<String> hashes = new ArrayList<>(List.of("1"));
        when(repository.getUniqueNumbers(QUANTITY))
                .thenReturn(list);
        when(encoder.encode(list))
                .thenReturn(hashes);

        generator.generateBatch();

        verify(repository).getUniqueNumbers(anyInt());
        verify(encoder).encode(list);
        verify(repository).saveAll(anyList());
    }

    @Test
    public void testGenerateBatchFromEmptyList() {
        ArrayList<Long> list = new ArrayList<>();
        ArrayList<String> hashes = new ArrayList<>();
        when(repository.getUniqueNumbers(QUANTITY))
                .thenReturn(list);
        when(encoder.encode(list))
                .thenReturn(hashes);

        generator.generateBatch();

        verify(repository).getUniqueNumbers(anyInt());
        verify(encoder).encode(list);
        verify(repository, never()).saveAll(anyList());
    }
}
