package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.config.сache.CacheProperties;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.cleanerService.CleanerService;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerServiceTest {

    private static final int EXPIRATION_URL = 1;

    private static final String HASH = "HASH";

    @InjectMocks
    private CleanerService cleanerService;

    @Mock
    private UrlService urlService;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private CacheProperties cacheProperties;
}
