package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Test
    @DisplayName("When method called then return List values")
    void whenMethodCalledThenNoThrownException() {
        urlService.findAndReturnExpiredUrls();

        verify(urlRepository)
                .findAndReturnExpiredUrls();
    }
}