package faang.school.urlshortenerservice.service.base62encoder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.nullability.NeverNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {

    @Value("${encoder.base62.chars:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789}")
    private static String BASE62_CHARS;
    private final static int BASE = 62;


    @Override
    public String encode(@Positive Long number) {
        StringBuilder encoded = new StringBuilder();
        do {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARS.charAt(remainder));
            number /= BASE;
        } while (number > 0);

        return encoded.reverse().toString();
    }

    @Override
    public List<String> encodeListNumbers(@NotNull Set<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }
}
