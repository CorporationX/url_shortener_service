package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {

    private final UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    private Url urlEntity;
    private UrlDto urlDto;
    private String url;
    private LocalDateTime date;

    @BeforeEach
    void setUp() {
        url = "https://ex.com/sg2c4";
        date = LocalDateTime.now();

        urlEntity = Url.builder()
                .hash(url)
                .url(url)
                .createdAt(date)
                .build();

        urlDto = UrlDto.builder()
                .hash(url)
                .url(url)
                .createdAt(date)
                .build();
    }

    @Test
    public void testEntityToDto() {
        UrlDto result = urlMapper.toDto(urlEntity);

        assertEquals(urlDto, result);
    }

    @Test
    public void testDtoToEntity() {
        Url result = urlMapper.toEntity(urlDto);

        assertEquals(urlEntity, result);
    }
}