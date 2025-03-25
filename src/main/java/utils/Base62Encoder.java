package utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Base62Encoder {
    private static final String BASE62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers){
        return numbers.stream()
                .map(Base62Encoder::encode)
                .toList();
    }

    public String encode(Long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0){
            int remainder = (int)(number % BASE62_CHARS.length());
            stringBuilder.append(BASE62_CHARS.charAt(remainder));
            number = number/BASE62_CHARS.length();
        }
        return stringBuilder.toString();
    }
}
