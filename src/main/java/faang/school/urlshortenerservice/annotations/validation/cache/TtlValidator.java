package faang.school.urlshortenerservice.annotations.validation.cache;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TtlValidator implements ConstraintValidator<ValidTtl, CacheProperties.Cache> {

    @Override
    public boolean isValid(CacheProperties.Cache cacheProps, ConstraintValidatorContext context) {
        return cacheProps != null && !ttlIsValid(cacheProps);
    }

    private boolean ttlIsValid(CacheProperties.Cache cacheProps) {
        return !(cacheProps.isTtlEnabled() && (cacheProps.getTtlInMinutes() == null || cacheProps.getTtlInMinutes() < 0));
    }
}
