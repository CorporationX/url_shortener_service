package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashBatchProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HashServiceImpl hashService;

    private List<Hash> hashesList;
    private List<String> stringList;
    private int limit;
    @BeforeEach
    public void setUp(){
        Hash hash = Hash.builder()
                .hash("187asr")
                .build();
        Hash hashOne = Hash.builder()
                .hash("091Las")
                .build();
        hashesList = List.of(hash, hashOne);
        stringList = List.of("985adr", "mcqi12", "0a0d3f");
        limit = 10;
    }
    @Test
    public void testSaveAllHashes(){
        when(hashRepository.saveAll(hashesList)).thenReturn(hashesList);

        List<Hash> resultHash = hashService.saveAllHashes(hashesList);

        assertNotNull(resultHash);
        assertEquals(2, resultHash.size());
        assertEquals("187asr", resultHash.get(0).getHash() );

        verify(hashRepository, times(1)).saveAll(hashesList);
    }

    @Test
    public void testSaveAllHash(){
        when(hashRepository.saveByHashList(stringList)).thenReturn(stringList);

        List<String> resultList = hashService.saveAllHash(stringList);

        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals("985adr", resultList.get(0));

        verify(hashRepository, times(1)).saveByHashList(stringList);
    }

}
