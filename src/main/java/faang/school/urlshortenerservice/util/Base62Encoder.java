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

    public List<String> encode(List<Long> randomNumbersList) {
        UniqueValuesListValidator.validateList(randomNumbersList, "Supplied list of numbers is empty!");
        UniqueValuesListValidator.validateUniqueness(randomNumbersList);

        return randomNumbersList.stream()
            .map(encoder::encode)
            .toList();
    }
}