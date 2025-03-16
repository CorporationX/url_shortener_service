package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UrlMapper {

    @Autowired
    ShortenerProperties shortenerProperties;

    @Mapping(target = "hash", source = "url.hash")
    @Mapping(target = "url", source = "url.url")
    @Mapping(target = "shortUrl", expression = "java(shortenerProperties.url().prefix() + url.getHash())")
    public abstract UrlResponseDto toUrlResponseDto(Url url);

    public abstract Url toUrl(UrlRequestDto urlRequestDto);

    /*@AfterMapping
    protected void setDefaultExpiredAtDate(UrlRequestDto urlRequestDto, @MappingTarget Url url) {
        if (urlRequestDto.expiredAtDate() == null) {
            url.setExpiredAtDate(LocalDateTime.now().plusDays(shortenerProperties.url().ttlDays()));
        } else {
            url.setExpiredAtDate(urlRequestDto.expiredAtDate());
        }
    }*/

}
