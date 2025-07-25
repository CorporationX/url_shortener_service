package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.model.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS", expression = "java(currentTimestamp())")
    Url toEntity(RequestUrlDto requestUrlDto);

    ResponseUrlDto toDto(Url url);

    default LocalDateTime currentTimestamp() {
        return LocalDateTime.now();
    }
}
