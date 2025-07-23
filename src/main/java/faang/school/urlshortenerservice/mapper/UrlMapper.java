package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlEncodeDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlMapper {

    @Mapping(target = "url", source = "urlEncodeDto.url")
    @Mapping(target = "hash", source = "hash")
    UrlDto toUrlDto(UrlEncodeDto urlEncodeDto, String hash);

    @Mapping(target = "url", source = "url")
    @Mapping(target = "hash", source = "hash")
    UrlDto toUrlDto(String url, String hash);

    @Mapping(target = "url", source = "urlEncodeDto.url")
    @Mapping(target = "hash", source = "hash")
    @Mapping(target = "createdAt", expression = "java(getNow())")
    Url toUrl(UrlEncodeDto urlEncodeDto, String hash);

    default LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
