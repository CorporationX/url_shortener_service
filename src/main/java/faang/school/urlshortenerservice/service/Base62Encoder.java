package faang.school.urlshortenerservice.service;

import java.util.List;

public interface Base62Encoder {
  List<String> encode(List<Long> numbers);
}
