package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final BaseEncoder encoder;

    public Base62Encoder(@Qualifier("configuredEncoder") BaseEncoder encoder) {
        this.encoder = encoder;
    }

    public List<String> encode(List<Long> numbers) {
        UniqueValuesListValidator.validateList(numbers, "Supplied list of numbers is empty!");
        UniqueValuesListValidator.validateUniqueness(numbers);

        return numbers.stream()
            .map(encoder::encode)
            .toList();
    }
}