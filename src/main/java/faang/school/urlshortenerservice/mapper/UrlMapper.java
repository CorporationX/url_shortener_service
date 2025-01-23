package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования сущностей {@link Url} в {@link RedisUrl}.
 * Используется для конвертации данных между сущностями базы данных и Redis.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    /**
     * Преобразует сущность {@link Url} в сущность {@link RedisUrl}.
     *
     * @param url Сущность {@link Url}, которую нужно преобразовать.
     * @return Преобразованная сущность {@link RedisUrl}.
     */
    RedisUrl toRedisUrl(Url url);
}
