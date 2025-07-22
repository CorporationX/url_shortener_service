package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {
    @Mock
    private HashRepository hashRepository;
    @Spy
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashService hashService;

    List<Hash> hashes = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        hashes.add(new Hash("abcopdd"));
        hashes.add(new Hash("efgpgkd"));
        hashes.add(new Hash("efgegkd"));
        hashes.add(new Hash("efiugkd"));
        hashes.add(new Hash("ljgpgkd"));
    }

    @Test
    public void testGetHashes_successfully() {
        int count = hashes.size();

        when(hashRepository.findAndDeleteLimit(count)).thenReturn(hashes);

        List<String> result = hashService.getHashesFromSequence(count);

        assertEquals(result, hashes.stream().map(Hash::getHash).toList());
        verify(hashRepository).findAndDeleteLimit(count);
        verify(hashRepository, never()).getNextSequenceValues(anyLong());
    }

    @Test
    public void testGetHashes_getAndGenerateNextSequenceValues() {
        int count = hashes.size();
        List<Long> sequenceNumber = List.of(456L, 457L);
        hashes.remove(hashes.size() - 1);
        hashes.remove(hashes.size() - 1);

        when(hashRepository.findAndDeleteLimit(count)).thenReturn(hashes);
        when(hashRepository.getNextSequenceValues(count - hashes.size())).thenReturn(sequenceNumber);

        List<String> result = hashService.getHashesFromSequence(count);

        assertEquals(result.size(), count);
        verify(hashRepository).findAndDeleteLimit(count);
        verify(hashRepository).getNextSequenceValues(count - hashes.size());
    }
}
