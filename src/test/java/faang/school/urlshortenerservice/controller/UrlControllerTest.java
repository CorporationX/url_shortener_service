package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataUrlValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    @Spy
    private UrlValidator urlValidator;

    private UrlDto urlDto;
    private String hash;

    @BeforeEach
    public void setUp() {
        urlDto = UrlDto.builder()
                .url("https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/32?selectedIssue=BJS2-17250")
                .build();
        hash = "dsf31d";

    }

    @Test
    public void testCreateShortLinkValidDto() {
        urlController.createShortLink(urlDto);

        verify(urlService, times(1)).createShortLink(urlDto);
    }

    @Test
    public void testRedirectToLongLinkValidHash() {
        urlController.redirectToLongLink(hash);

        verify(urlService, times(1)).getLongLink(hash);
    }

    @Test
    public void testRedirectToLongLinkNotValidHash() {
        assertThrows(DataUrlValidationException.class, () -> urlController.redirectToLongLink("   "));
        verify(urlService, times(0)).getLongLink(hash);
    }
}
