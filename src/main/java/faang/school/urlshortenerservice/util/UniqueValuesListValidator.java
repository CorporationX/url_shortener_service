package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.List;

@UtilityClass
public class UniqueValuesListValidator {
    public void validateList(List<?> list, String errorMessage) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateUniqueness(List<?> list) {
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalArgumentException("Supplied list contains duplicate values!");
        }
    }
}
