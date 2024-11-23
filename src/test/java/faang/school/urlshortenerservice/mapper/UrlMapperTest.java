package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlMapperTest {

    @InjectMocks
    private UrlMapperImpl urlMapper;

    private static final String URL = "url";
    private static final String HASH = "hash";
    private static final String DOMAIN = "domain";

    @Test
    @DisplayName("Success when Url mapping to UrlDto")
    public void toDto() {
        Url url = Url.builder()
                .hash(HASH)
                .url(URL)
                .build();

        UrlDto result = urlMapper.toDto(url, DOMAIN);

        assertNotNull(result);
        assertEquals(DOMAIN + url.getHash(), result.getUrl());
    }

    @Test
    @DisplayName("Success mapping to Url")
    public void toEntity() {
        UrlDto urlDto = UrlDto.builder()
                .url(URL)
                .build();

        Url result = urlMapper.toEntity(urlDto, HASH);

        assertNotNull(result);
        assertEquals(urlDto.getUrl(), result.getUrl());
        assertEquals(HASH, result.getHash());
    }
}