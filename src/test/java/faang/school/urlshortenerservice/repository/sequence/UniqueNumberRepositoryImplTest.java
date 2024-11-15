package faang.school.urlshortenerservice.repository.sequence;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.hash.impl.FreeHashRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueNumberRepositoryImplTest {

    private static final int NUMBER_OF_BATCH_TO_INSERT = 5;
    private static final int NUMBER_OF_STRINGS_TO_INSERT = 15;

    @InjectMocks
    private FreeHashRepositoryImpl freeHashRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private HashConfig hashConfig;

    @Test
    @DisplayName("When method is called, then should save list of hashes certain times")
    void whenMethodIsCalledThenCertainTimesCallsSaveMethod() {
/*        when(hashConfig.getInsertBatch())
                .thenReturn(NUMBER_OF_BATCH_TO_INSERT);

        freeHashRepository.saveHashes(generateRandomStrings());

        verify(entityManager, times(NUMBER_OF_STRINGS_TO_INSERT))
                .merge(anyString());
        verify(entityManager, times(NUMBER_OF_BATCH_TO_INSERT))
                .flush();*/
    }

    @Test
    @DisplayName("When method is called, then should return list of String less or equals range")
    void whenMethodIsCalledThenReturnListOfStringsLessOrEqualsRange() {/*

        List<String> freeHashesInDataBase = hashService.getHashBatch();

        System.out.println(freeHashesInDataBase.size());

        asser(numberSequenceConfig.getRange(), numbersFromDataBaseSequence.size());*/
    }

    private static List<String> generateRandomStrings() {
        List<String> randomStrings = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_STRINGS_TO_INSERT; i++) {
            randomStrings.add(UUID.randomUUID().toString());
        }
        return randomStrings;
    }
}