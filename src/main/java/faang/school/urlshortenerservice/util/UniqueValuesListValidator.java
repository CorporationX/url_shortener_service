package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.List;

@UtilityClass
public class UniqueValuesListValidator {
    public static <T> void validateList(List<T> list, String errorMessage) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T> void validateUniqueness(List<T> list) {
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalArgumentException("Supplied list contains duplicate values!");
        }
    }
}
