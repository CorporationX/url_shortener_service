package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlMapperTest {

    private UrlMapper urlMapper = new UrlMapperImpl();

    private Url entity;
    private UrlDto dto;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final String url = "https://www.oracle.com/cis/";
    private final String hash = "6FgV8";

    @BeforeEach
    void setUp() {
        entity = Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(createdAt)
                .build();
        dto = UrlDto
                .builder()
                .hash(hash)
                .url(url)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void toDtoTest() {
        UrlDto result = urlMapper.toDto(entity);
        assertEquals(dto, result);
    }

    @Test
    void toEntityTest() {
        Url result = urlMapper.toEntity(dto);
        assertEquals(entity, result);
    }
}