package baum.urlshortenerservice.validator;

import java.lang.reflect.Field;

public interface FieldValidator {
    void validate(Object param, Field field);
}
