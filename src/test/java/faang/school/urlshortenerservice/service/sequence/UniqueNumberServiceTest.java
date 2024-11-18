package faang.school.urlshortenerservice.service.sequence;

import faang.school.urlshortenerservice.repository.sequence.UniqueNumberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UniqueNumberServiceTest {

    @InjectMocks
    private UniqueNumberService uniqueNumberService;

    @Mock
    private UniqueNumberRepository uniqueNumberRepository;

    @Test
    @DisplayName("When method called then return List values")
    void whenMethodCalledThenNoThrownException() {
        uniqueNumberService.getUniqueNumbers();

        verify(uniqueNumberRepository)
                .getUniqueNumbers();
    }

}