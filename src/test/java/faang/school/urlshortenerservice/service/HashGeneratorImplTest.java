package faang.school.urlshortenerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashGeneratorImplTest {

  @InjectMocks
  private HashGeneratorImpl hashGenerator;
  @Mock
  private HashRepository hashRepository;
  @Mock
  private Base62Encoder base62Encoder;
  @Captor
  private ArgumentCaptor<List<String>> hashesArgumentCaptor;

  @Test
  @DisplayName("generate and save hashes into DB")
  void testGenerateBatch() {
    List<Long> uniqueNumbers = List.of(10001L, 10002L, 10003L, 10004L);
    List<String> hashes = List.of("2Bj", "2Bk", "2Bl", "2Bm");

    when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
    when(base62Encoder.encode(uniqueNumbers)).thenReturn(hashes);

    hashGenerator.generateBatch();

    verify(hashRepository, times(1)).getUniqueNumbers(anyInt());
    verify(base62Encoder, times(1)).encode(uniqueNumbers);
    verify(hashRepository, times(1)).save(hashesArgumentCaptor.capture());

    assertEquals(hashes, hashesArgumentCaptor.getValue());
  }
}