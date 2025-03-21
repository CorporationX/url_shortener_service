package faang.school.urlshortenerservice.service;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder implements BaseEncoder {

    private final Base62 base62;

    @Override
    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        for (Long number : numbers) {
            byte[] encoded = base62.encode(number.toString().getBytes());
            hashes.add(new String(encoded));
        }
        return hashes;
    }
}