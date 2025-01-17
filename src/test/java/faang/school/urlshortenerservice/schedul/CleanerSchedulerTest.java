package faang.school.urlshortenerservice.schedul;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Captor
    private ArgumentCaptor<List<Hash>> captor;

    @Test
    void testRemoveOldUrl() {
        Url url = new Url();
        url.setHash("aaaa");

        when(urlRepository.findAndRemoveAllOldEntity()).thenReturn(List.of(url));

        cleanerScheduler.removeOldUrl();

        verify(urlRepository).findAndRemoveAllOldEntity();
        verify(hashRepository).saveAll(captor.capture());

        List<Hash> result = captor.getValue();

        assertEquals(result.get(0).getHash(), url.getHash());
    }
}