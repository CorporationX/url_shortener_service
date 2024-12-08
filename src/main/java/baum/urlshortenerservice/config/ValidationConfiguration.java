package baum.urlshortenerservice.config;

import baum.urlshortenerservice.validator.AnnotationBasedParamValidatorImpl;
import baum.urlshortenerservice.validator.FieldValidator;
import baum.urlshortenerservice.validator.ParamValidator;
import baum.urlshortenerservice.validator.UrlValidator;
import baum.urlshortenerservice.validator.annotaiton.Url;
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
