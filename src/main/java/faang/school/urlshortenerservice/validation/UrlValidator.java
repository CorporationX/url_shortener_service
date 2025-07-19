package faang.school.urlshortenerservice.validation;

import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.common.PreConditionFailedException;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class UrlValidator {

    public void validateNotExpired(ShortUrl shortUrl) {
        if(Objects.nonNull(shortUrl.getExpirationTime()) && shortUrl.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new PreConditionFailedException("Short url is expired.");
        }
    }
}