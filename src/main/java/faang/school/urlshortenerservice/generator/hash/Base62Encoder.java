package faang.school.urlshortenerservice.generator.hash;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    Base62 base62 = Base62.createInstance();

    public synchronized List<String> encode(List<Long> numbers) {
        return numbers.stream().map(String::valueOf).map(i -> new String(base62.encode(i.getBytes()))).toList();
    }
}
