package faang.school.urlshortenerservice.utilities;

import org.springframework.stereotype.Component;
import java.util.List;
@Component
public interface Base62Encoder {
    List<String> encode(List<Long> numbers);
    String coding (long incomingNumber);
}
