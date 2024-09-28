package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlMapperTest {
    private final UrlMapperImpl urlMapper = new UrlMapperImpl();
    private Url url;
    private UrlDto urlDto;

    @BeforeEach
    void setUp() {
        url = Url.builder()
                .url("url")
                .hash("hash")
                .build();

        urlDto = UrlDto.builder()
                .url("url")
                .hash("hash")
                .build();
    }

    @Test
    void testToDto() {
        UrlDto urlDtoMapped = urlMapper.toDto(url);

        assertEquals(urlDto.getUrl(), urlDtoMapped.getUrl());
        assertEquals(urlDto.getHash(), urlDtoMapped.getHash());
    }

    @Test
    void testToEntity() {
        Url urlMapped = urlMapper.toEntity(urlDto);

        assertEquals(url.getUrl(), urlMapped.getUrl());
        assertEquals(url.getHash(), urlMapped.getHash());
        assertEquals(url.getCreatedAt(), urlMapped.getCreatedAt());
    }
}