package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaseEncoderImpl implements BaseEncoder {

    @Value("${encoder.string}")
    private String baseString;

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .collect(Collectors.toList());
    }

    private String encodeToBase62(Long number) {
        StringBuilder encoded = new StringBuilder();

        int baseSize = baseString.length();
        while (number > 0){
            int remainder = (int) (number % baseSize);
            encoded.insert(0, baseString.charAt(remainder));
            number /= baseSize;
        }
        return encoded.toString();
    }
}
