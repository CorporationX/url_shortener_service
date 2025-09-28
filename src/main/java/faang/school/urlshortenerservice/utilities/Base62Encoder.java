package faang.school.urlshortenerservice.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private final String BASE62_ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final long size_of_base = BASE62_ALL_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::codingProcess).toList();
    }

    public String codingProcess(long processingNumber) {
        StringBuilder toEncode = new StringBuilder();
        while (processingNumber > 0) {
            int cursor = (int) (processingNumber % size_of_base);
            toEncode.insert(0, BASE62_ALL_CHARS.charAt(cursor));
            processingNumber /= size_of_base;
        }
        return toEncode.toString();
    }

}
