package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlViewDto;
import faang.school.urlshortenerservice.exception.ForbiddenException;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для работы с сокращением URL.
 * Обеспечивает создание коротких ссылок и перенаправление на оригинальные URL.
 * Использует кэширование для повышения производительности.
 *
 * @author bozya
 * @since 18.09.2025
 */
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hachCache;
    private final UrlMapper mapper;

    /**
     * Возвращает оригинальный URL по хэшу.
     * Поиск выполняется сначала в кэше, затем в базе данных.
     *
     * @param hash хэш короткой ссылки
     * @return оригинальный URL
     * @throws HashNotFoundException если хэш не найден
     */
    public String getOriginalUrl(String hash) {

        String urlFromCache = urlCacheRepository.findUrlByHash(hash);
        if (urlFromCache != null) {
            return urlFromCache;
        }

        String urlFromDb = urlRepository.findUrlByHash(hash)
                .orElseThrow(() -> new HashNotFoundException(hash));

        urlCacheRepository.save(hash, urlFromDb);

        return urlFromDb;
    }

    /**
     * Создает короткую ссылку для указанного URL.
     * Если URL уже был сокращен, возвращает существующую короткую ссылку.
     *
     * @param dto DTO с оригинальным URL
     * @return DTO с короткой ссылкой
     * @throws ForbiddenException если нет доступных хэшей
     */
    @Transactional
    public UrlViewDto createShortUrl(CreateShortUrlDto dto) {
        Optional<UrlEntity> existing = urlRepository.findByUrl(dto.longUrl());
        if (existing.isPresent()) {
            return mapper.toViewDto(existing.get());
        }

        String hash = hachCache.getHash();
        if (hash == null) throw new ForbiddenException("Ошибка с хэшем");

        UrlEntity entity = mapper.toEntity(dto);
        entity.setHash(hash);
        entity.setCreatedAt(LocalDateTime.now());

        urlRepository.save(entity);
        urlCacheRepository.save(hash, dto.longUrl());

        return mapper.toViewDto(entity);
    }
}