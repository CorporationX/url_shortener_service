package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.FreeHashRepository;
import faang.school.urlshortenerservice.service.sequence.UniqueNumberService;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @InjectMocks
    private HashService hashService;

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private UniqueNumberService uniqueNumberService;

    @Test
    @DisplayName("When method is called, then should save list of hashes")
    void whenMethodIsCalledThenCallsSaveMethod() {
        hashService.saveRangeHashes(anyList());

        verify(freeHashRepository)
                .saveHashes(anyList());
    }

    @Test
    @DisplayName("When method is called, then should return list")
    void whenMethodIsCalledThenReturnListOfStringsLessOrEqualsRange() {
        hashService.getHashes();

        verify(freeHashRepository)
                .findAndDeleteFreeHashes(anyInt());
    }

    @Test
    @DisplayName("When called then should convert list of numbers to list of strings")
    void whenMethodCalledThenNotThrownException() {
        hashService.generateBatchHash();

        verify(uniqueNumberService)
                .getUniqueNumbers();
        verify(base62Encoder)
                .encodeListNumbers(anyList());
        verify(hashService)
                .saveRangeHashes(anyList());
    }
}
