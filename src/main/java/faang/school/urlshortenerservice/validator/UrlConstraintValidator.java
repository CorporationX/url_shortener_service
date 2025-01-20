package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.annotation.ValidUrl;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlConstraintValidator implements ConstraintValidator<ValidUrl, String> {

    private final RedisTemplate<String, String> blackListRedisTemplate;

    private UrlValidator urlValidator;

    @PostConstruct
    void init() {
        urlValidator = new UrlValidator(new String[]{"http", "https"});
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return !Boolean.TRUE.equals(blackListRedisTemplate.hasKey(url)) && urlValidator.isValid(url);
    }
}
