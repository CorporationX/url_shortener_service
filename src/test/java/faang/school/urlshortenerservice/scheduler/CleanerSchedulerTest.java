package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Captor
    ArgumentCaptor<List<Hash>> hashListArgumentCaptor;

    List<String> oldHashes;
    List<Hash> newlySavedHashes;

    @BeforeEach
    void init() {
        oldHashes = new ArrayList<>();
        oldHashes.add("12345");
        oldHashes.add("67891");

        newlySavedHashes = oldHashes.stream().map(Hash::new).toList();
    }


    @Test
    public void deleteUrlAssociationsWithExistingHashesToBeDeletedTest() {
        when(urlRepository.deleteUrlAssociationByTime()).thenReturn(oldHashes);
        when(hashRepository.saveAll(any())).thenReturn(newlySavedHashes);

        cleanerScheduler.deleteOldUrlAssociations();

        verify(urlRepository, only()).deleteUrlAssociationByTime();
        verify(hashRepository, only()).saveAll(hashListArgumentCaptor.capture());
        List<Hash> capturedHashList = hashListArgumentCaptor.getValue();
        assertEquals(newlySavedHashes, capturedHashList);

    }

    @Test
    public void deleteUrlAssociationsWithoutExistingHashesToBeDeletedTest() {
        oldHashes.clear();
        when(urlRepository.deleteUrlAssociationByTime()).thenReturn(oldHashes);

        cleanerScheduler.deleteOldUrlAssociations();

        verify(urlRepository, only()).deleteUrlAssociationByTime();
        verify(hashRepository, never()).saveAll(any());

    }

    @Test
    public void deleteUrlAssociationsRollbackOnErrorTest() {
        when(urlRepository.deleteUrlAssociationByTime()).thenThrow(new RuntimeException("Database Error"));

        try {
            cleanerScheduler.deleteOldUrlAssociations();
        } catch (RuntimeException e) {

        }

        verify(hashRepository, never()).saveAll(any());
    }


}
