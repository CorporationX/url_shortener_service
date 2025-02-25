package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Urls;
import faang.school.urlshortenerservice.repository.interfaces.UrlsJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class UrlsRepository {
    final private UrlsJpaRepository urlsJpaRepository;

    final private MessageSource messageSource;

    public Urls findByHash(String hash) {
        return urlsJpaRepository.findByHash(hash).orElseThrow(() ->
                new EntityNotFoundException(
                        messageSource.getMessage("exception.entity.not.found.text",
                                new Object[]{hash},
                                LocaleContextHolder.getLocale())));
    }
}