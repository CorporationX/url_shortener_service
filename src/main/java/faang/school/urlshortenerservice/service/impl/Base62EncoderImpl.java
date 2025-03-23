package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.service.Base62Encoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {

  private static final String BASE_62_CHARACTERS =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int BASE_LENGTH = BASE_62_CHARACTERS.length();

  public List<String> encode(List<Long> numbers) {
    return numbers.stream()
        .map(number -> {
          if (number == 0) {
            return String.valueOf(BASE_62_CHARACTERS.charAt(0));
          }
          StringBuilder result = new StringBuilder();
          long temporarNumber = number;
          while (temporarNumber > 0) {
            result.insert(0, BASE_62_CHARACTERS.charAt((int) (temporarNumber % BASE_LENGTH)));
            temporarNumber /= BASE_LENGTH;
          }
          return result.toString();
        })
        .toList();
  }
}
