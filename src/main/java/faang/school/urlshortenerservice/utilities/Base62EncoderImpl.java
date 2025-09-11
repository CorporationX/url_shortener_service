package faang.school.urlshortenerservice.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final long base_size = BASE62_CHARS.length();
    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::coding).toList();
    }
    @Override
    public String coding(long incomingNumber) {
        StringBuilder toEncode = new StringBuilder();
        while (incomingNumber > 0) {
            int cursor = (int) (incomingNumber % base_size);
            toEncode.insert(0, BASE62_CHARS.charAt(cursor));
            incomingNumber /= base_size;
        }
        return toEncode.toString();
    }

}
