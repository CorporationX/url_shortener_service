package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.model.UrlEntity;

/**
 * UrlViewDto — dto для сущности {@link UrlEntity}
 *
 * @author bozya
 * @since 20.09.2025
 */
public record UrlViewDto(String shortUrl) {
}