package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final String url = "https://www.oracle.com/cis/";
    private final String hash = "6FgV8";
    private final String serverAddress = "https://sh.c/";

    @Test
    void generateRedirectTest() {
        ReflectionTestUtils.setField(urlController, "serverAddress", serverAddress);
        UrlDto request = UrlDto
                .builder()
                .url(url)
                .build();
        UrlDto response = UrlDto
                .builder()
                .hash(hash)
                .url(url)
                .createdAt(createdAt)
                .build();

        UrlDto expected = UrlDto.builder()
                .hash(hash)
                .url(serverAddress + hash)
                .createdAt(createdAt)
                .build();

        when(urlService.associateHashWithURL(request)).thenReturn(response);

        UrlDto result = urlController.generateRedirect(request);

        assertEquals(expected, result);

        verify(urlService).associateHashWithURL(request);
    }
}