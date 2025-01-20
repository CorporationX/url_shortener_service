package faang.school.urlshortenerservice.schedul;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    @InjectMocks
    private Scheduler scheduler;
    @Mock
    private UrlService urlService;
    @Mock
    private HashGenerator generator;

    @Test
    void testRemoveOldUrl(){
        scheduler.removeOldUrl();
        verify(urlService).removeOldUrl();
    }

    @Test
    void testGenerateHash(){
        scheduler.generateHash();
        verify(generator).generateHash();
    }
}