package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Captor
    private ArgumentCaptor<List<String>> captor;

    @Test
    void testHashGenerate() {
        List<Long> numbers = new ArrayList<>();
        numbers.add(1L);
        numbers.add(2L);
        numbers.add(3L);
        numbers.add(4L);
        numbers.add(5L);
        numbers.add(6L);
        numbers.add(7L);
        numbers.add(8L);
        numbers.add(9L);
        numbers.add(10L);

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(numbers);

        hashGenerator.generateBatch();

        verify(hashRepository).save(captor.capture());
        captor.getValue().forEach(System.out::println);
        assertEquals(10, captor.getValue().size());
    }
}
