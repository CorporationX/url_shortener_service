package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCleanerServiceTest {
    private static final int DAYS = 30;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private UrlCleanerService urlCleanerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCleanerService, "days", DAYS);
    }

    @Test
    void testRemoveExpiredUrlsAndResaveHashes() {
        List<String> hashes = List.of("abc123", "xyz456");
        List<Hash> hashEntities = hashes.stream().map(Hash::new).toList();

        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(hashes);

        urlCleanerService.removeExpiredUrlsAndResaveHashes();

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes(any(LocalDateTime.class));
        verify(hashRepository, times(1)).saveAll(hashEntities);
    }
}