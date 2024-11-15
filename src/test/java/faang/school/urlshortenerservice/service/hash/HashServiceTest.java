package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.hash.FreeHashRepository;
import faang.school.urlshortenerservice.service.sequence.UniqueNumberService;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    private static final int ONE = 1;
    private static final int SELECT_BATCH = 2;

    @InjectMocks
    private HashService hashService;

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private UniqueNumberService uniqueNumberService;

    @Mock
    private HashConfig hashConfig;


    @Test
    @DisplayName("When method is called, then should save list of hashes")
    void whenMethodIsCalledThenCallsSaveMethod() {
        hashService.saveRangeHashes(anyList());

        verify(freeHashRepository)
                .saveHashes(anyList());
    }

    @Test
    @DisplayName("When method is called and this list more than properties then should return list")
    void whenHashesFromDbMoreThanPropThenReturnList() {
        List<String> hashesFromDbEnough = List.of("Str", "Str", "Str");

        when(hashConfig.getSelectBatch())
                .thenReturn(SELECT_BATCH);
        when(freeHashRepository.findAndDeleteFreeHashes(anyInt()))
                .thenReturn(hashesFromDbEnough);

        hashService.getHashes();

        verify(freeHashRepository)
                .findAndDeleteFreeHashes(anyInt());
    }

    @Test
    @DisplayName("When method is called and this list less than properties then should generate new and return list")
    void whenHashesFromDbLessThanPropThenGenerateNewAndReturnList() {
        List<String> hashesFromDbNotEnough = List.of("Str");

        when(hashConfig.getSelectBatch())
                .thenReturn(SELECT_BATCH);
        when(freeHashRepository.findAndDeleteFreeHashes(anyInt()))
                .thenReturn(hashesFromDbNotEnough);

        hashService.getHashes();

        verify(freeHashRepository)
                .findAndDeleteFreeHashes(anyInt());
        verify(uniqueNumberService)
                .getUniqueNumbers();
        verify(base62Encoder)
                .encodeNumbersInBase62(anyList());
        verify(freeHashRepository)
                .saveHashes(anyList());
    }

    @Test
    @DisplayName("When called then should convert list of numbers to list of strings")
    void whenMethodCalledThenNotThrownException() {
        hashService.generateBatchHash(ONE);

        verify(uniqueNumberService)
                .getUniqueNumbers();
        verify(base62Encoder)
                .encodeNumbersInBase62(anyList());
        verify(freeHashRepository)
                .saveHashes(anyList());
    }
}
