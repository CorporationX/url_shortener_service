/*
package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FreeHashRepositoryImplTest {

    private static final int NUMBER_OF_BATCH_TO_INSERT = 5;
    private static final int NUMBER_OF_STRINGS_TO_INSERT = 15;

    @Autowired
    private FreeHashRepositoryImpl freeHashRepository;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private HashConfig hashConfig;

    @Test
    @DisplayName("When method is called, then should save list of hashes certain times")
    void whenMethodIsCalledThenCertainTimesCallsSaveMethod() {
        when(hashConfig.getInsertBatch())
                .thenReturn(NUMBER_OF_BATCH_TO_INSERT);
        //doNothing().when(entityManager.merge(anyString()));

        freeHashRepository.saveHashes(generateRandomStrings());

        verify(entityManager, times(NUMBER_OF_STRINGS_TO_INSERT))
                .merge(anyString());
        verify(entityManager, times(NUMBER_OF_BATCH_TO_INSERT))
                .flush();
    }

    @Test
    @DisplayName("When method is called, then should return list of String less or equals range")
    void whenMethodIsCalledThenReturnListOfStringsLessOrEqualsRange() {


        */
/*List<String> freeHashesInDataBase = hashService.getHashBatch();

        System.out.println(freeHashesInDataBase.size());

        asser(numberSequenceConfig.getRange(), numbersFromDataBaseSequence.size());*//*

    }

    private static List<String> generateRandomStrings() {
        List<String> randomStrings = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_STRINGS_TO_INSERT; i++) {
            randomStrings.add(UUID.randomUUID().toString());
        }
        return randomStrings;
    }
}*/
