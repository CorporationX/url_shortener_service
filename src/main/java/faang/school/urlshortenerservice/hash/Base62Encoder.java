package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        log.info("Encoder got List with size {} and begins encoding.", numbers.size());
        List<String> result = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder stringBuilder = new StringBuilder();
            while (number > 0) {
                stringBuilder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length()))).reverse();
                number /= BASE_62_CHARACTERS.length();
            }
            result.add(stringBuilder.toString());
        }
        return result;
    }
}
