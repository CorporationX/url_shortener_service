package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @Spy
    private UrlMapper urlMapper;
    @Mock
    private UrlUtilService urlServiceMock;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test URL validate")
    void testCreateShortUrl() {
        UrlRequestDto urlRequestDto = UrlRequestDto.builder()
                .url("htp:/234")
                .build();
        UrlResponseDto urlResponseDto = UrlResponseDto.builder()
                .url("11")
                .build();
        Mockito.when(urlServiceMock.shortenUrl(Mockito.any())).thenReturn(urlResponseDto);
        urlShortenerController.createShortUrl(urlRequestDto);
    }
}