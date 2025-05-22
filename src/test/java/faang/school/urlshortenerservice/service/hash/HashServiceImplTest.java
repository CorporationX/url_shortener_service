package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceImplTest {

    private HashServiceImpl hashService;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    private final HashProperties hashProperties = new HashProperties();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<String> fullHashes = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    @BeforeEach
    void setUp() {
        hashProperties.setGet(HashProperties.Get.builder().max(10).min(6).build());
        hashService = new HashServiceImpl(hashRepository, hashGenerator, hashProperties, executor);
    }

    @Test
    void getHashes_ShouldGetAndNotGenerate() {
        when(hashRepository.findAndDelete(hashProperties.getGet().getMax())).thenReturn(fullHashes);
        List<String> hashes = hashService.getHashes();

        assertEquals(fullHashes, hashes);
        verify(hashRepository, times(1)).findAndDelete(hashProperties.getGet().getMax());
        verify(hashGenerator, never()).generate();

    }

    @Test
    void getHashes_ShouldGetAndGenerateWhenGivenHashesLessThanMin() {
        List<String> hashes = List.of("1", "2", "3", "4", "5");
        when(hashRepository.findAndDelete(hashProperties.getGet().getMax())).thenReturn(hashes);
        List<String> givenHashes = hashService.getHashes();

        assertEquals(hashes, givenHashes);
        verify(hashRepository, times(1)).findAndDelete(hashProperties.getGet().getMax());
        verify(hashGenerator, times(1)).generate();
    }

    @Test
    void getHashes_ShouldGetAndGenerateAndBlockWhenGivenHashesIsEmpty() {
        List<String> hashes = new ArrayList<>();
        when(hashRepository.findAndDelete(hashProperties.getGet().getMax())).thenReturn(hashes);
        when(hashGenerator.generate()).thenAnswer(invocation -> {
            hashes.addAll(fullHashes);
            return hashes;
        });
        List<String> givenHashes = hashService.getHashes();

        assertEquals(fullHashes, givenHashes);
        verify(hashRepository, times(2)).findAndDelete(hashProperties.getGet().getMax());
        verify(hashGenerator, times(1)).generate();
    }
}