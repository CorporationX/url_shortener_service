package faang.school.urlshortenerservice.scheduler.cleaner;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.hash.HashMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    private static final Integer EXPIRATION_INTERVAL = 1;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashMapper hashMapper;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "expirationInterval", EXPIRATION_INTERVAL);
    }

    @Test
    @DisplayName("Should clean old URLs and save hashes")
    void whenCleanOldDataThenShouldDeleteExpiredUrlsAndSaveHashes() {
        List<String> stringHashList = List.of("hash1", "hash2", "hash3");
        when(urlRepository.deleteExpiredUrlsAndReturnHashes(any())).thenReturn(stringHashList);
        when(hashMapper.toEntity(any(HashDto.class))).thenAnswer(invocation -> new Hash());

        cleanerScheduler.cleanOldData();

        verify(urlRepository).deleteExpiredUrlsAndReturnHashes(any());
        verify(hashMapper, times(stringHashList.size())).toEntity(any(HashDto.class));
        verify(hashRepository).saveAll(any());
    }
}

