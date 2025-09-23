package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlViewDto;
import faang.school.urlshortenerservice.model.UrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * UrlMapper — маппер для преобразования сущности {@link UrlEntity} в dto и наоборот.

 *
 * @author bozya
 * @since 20.09.2025
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlMapper {

    @Mapping(target = "url", source = "longUrl")
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UrlEntity toEntity(CreateShortUrlDto dto);

    @Mapping(target = "shortUrl", expression = "java(\"https://short.com/\" + entity.getHash())")
    UrlViewDto toViewDto(UrlEntity entity);
}