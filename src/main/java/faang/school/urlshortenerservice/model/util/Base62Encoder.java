package faang.school.urlshortenerservice.model.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    @Value("${base62.alphabet}")
    @NotNull(message = "BASE62 must be specified")
    @NotBlank(message = "BASE62 must be not blank")
    private String BASE62;

    @Value("${base62.length}")
    @NotNull(message = "BASE62 length must be not null")
    @Min(value = 62, message = "BASE62 length must be equal 62")
    @Max(value = 62, message = "BASE62 length must be equal 62")
    private Integer base62Length;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    public String encodeNumber(long num) {
        StringBuilder encoded = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % base62Length);
            encoded.append(BASE62.charAt(remainder));
            num /= base62Length;
        }
        return encoded.toString();
    }
}
