package faang.school.urlshortenerservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaseEncoderImpl implements BaseEncoder {

    @Value("${hash.generator.encoder-base62}")
    private String base;

    @Override
    public List<String> encode(List<Long> numbers) {
        log.info("Encoding {} numbers to base62", numbers.size());
        return numbers.stream()
                .map(this::encodeToBase62)
                .collect(Collectors.toList());
    }

    private String encodeToBase62(Long number) {
        StringBuilder encoded = new StringBuilder();
        int baseSize = base.length();
        while (number > 0){
            int remainder = (int) (number % baseSize);
            encoded.insert(0, base.charAt(remainder));
            number /= baseSize;
        }
        log.info("Encoded {} to base62: {}", number, encoded);
        return encoded.toString();
    }
}
