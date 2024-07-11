package faang.school.urlshortenerservice.service.uniquenumber;

import faang.school.urlshortenerservice.repository.uniquenumber.UniqueNumberRepositoryCustom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniqueNumberTest {
    @InjectMocks
    public UniqueNumber uniqueNumber;

    @Mock
    public UniqueNumberRepositoryCustom uniqueNumberRepository;

    long lastUniqueNumberInBD = 1L;
    long count = 3L;

    @Test
    public void encodeTest() {
        when(uniqueNumberRepository.getLastUniqueNumber()).thenReturn(lastUniqueNumberInBD);

        long finalNumber = lastUniqueNumberInBD + count;
        List<Long> numbers = LongStream.rangeClosed(lastUniqueNumberInBD, finalNumber - 1).boxed().toList();

        List<Long> result = uniqueNumber.getUniqueNumbers(count);

        verify(uniqueNumberRepository, times(1)).setLastUniqueNumber(finalNumber);
        assertEquals(numbers, result);
    }
}
