package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Captor
    private ArgumentCaptor<List<Hash>> hashCaptor;

    @Test
    public void whenCleanUrls_OldUrlsDeletedAndHashesSaved() {
        List<String> oldHashes = List.of("hash1", "hash2");
        when(urlRepository.deleteOlderThanOneYearUrl()).thenReturn(oldHashes);

        cleanerScheduler.cleanUrls();

        verify(urlRepository).deleteOlderThanOneYearUrl();
        verify(hashRepository).saveAll(hashCaptor.capture());

        List<Hash> capturedHashes = hashCaptor.getValue();
        assertNotNull(capturedHashes);
        assertEquals(oldHashes.size(), capturedHashes.size());
        assertEquals(oldHashes.get(0), capturedHashes.get(0).getHash());
        assertEquals(oldHashes.get(1), capturedHashes.get(1).getHash());
    }
}