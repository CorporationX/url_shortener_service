package faang.school.urlshortenerservice.hash;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private final Base62 base62;

    public List<String> encode(List<Long> nums) {
        return null;
    }
}
