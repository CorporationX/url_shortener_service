package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.UrlNotRecognizedException;
import faang.school.urlshortenerservice.validator.annotaiton.Url;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UrlValidator implements FieldValidator {
    @Override
    public void validate(Object entity, Field field) {
        if (String.class.isAssignableFrom(field.getType())) {
            Url annotation = field.getAnnotation(Url.class);
            String regex = annotation.value();
            try {
                String fieldValue = (String) field.get(entity);
                if (fieldValue != null && !fieldValue.matches(regex)) {
                    throw new UrlNotRecognizedException(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new ValidationException(e);
            }
        }
    }
}
