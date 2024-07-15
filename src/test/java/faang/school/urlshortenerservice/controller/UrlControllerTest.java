package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        urlDto = UrlDto.builder()
                .url("https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/32?selectedIssue=BJS2-17250")
                .build();
    }

    @Test
    public void testCreateShortLinkValidDto() {
        urlController.createShortLink(urlDto);

        verify(urlService, times(1)).createShortLink(urlDto);
    }
}
