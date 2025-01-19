//package faang.school.urlshortenerservice.util;
//
//import faang.school.urlshortenerservice.model.Hash;
//import faang.school.urlshortenerservice.service.HashService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class HashGeneratorTest {
//
//    @Mock
//    private HashService hashService;
//
//    @Mock
//    private BaseEncoder baseEncoder;
//
//    @InjectMocks
//    private HashGenerator hashGenerator;
//
//    private Long batchSize;
//    private List<Long> numbers;
//    private List<Hash> encodedHashes;
//
//
//    @BeforeEach
//    void setUp() {
//        batchSize = 10L;
//        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
//        encodedHashes = new ArrayList<>(
//                List.of(Hash.builder().hash("1").build(),
//                        Hash.builder().hash("2").build(),
//                        Hash.builder().hash("3").build()));
//    }
//
//    @Test
//    void testGenerateHashBatch() {
//        when(hashService.getUniqueSeqNumbers(batchSize)).thenReturn(numbers);
//        when(baseEncoder.encodeList(numbers)).thenReturn(encodedHashes);
//        when(hashService.saveHashes(encodedHashes)).thenReturn(encodedHashes);
//
//        hashGenerator.asyncHashRepositoryRefill();
//
//        verify(hashService, times(1)).getUniqueSeqNumbers(batchSize);
//        verify(baseEncoder, times(1)).encodeList(numbers);
//        verify(hashService, times(1)).saveHashes(encodedHashes);
//    }
//}