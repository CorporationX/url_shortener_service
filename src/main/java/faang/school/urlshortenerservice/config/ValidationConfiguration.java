package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.validator.AnnotationBasedParamValidatorImpl;
import faang.school.urlshortenerservice.validator.FieldValidator;
import faang.school.urlshortenerservice.validator.ParamValidator;
import faang.school.urlshortenerservice.validator.UrlValidator;
import faang.school.urlshortenerservice.validator.annotaiton.Url;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ValidationConfiguration {
    @Bean
    public ParamValidator paramValidator(UrlValidator urlValidator) {
        Map<Class<? extends Annotation>, FieldValidator> map = new HashMap<>();
        map.put(Url.class, urlValidator);
        return new AnnotationBasedParamValidatorImpl(map);
    }
}
