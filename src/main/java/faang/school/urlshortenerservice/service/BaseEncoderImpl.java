package faang.school.urlshortenerservice.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@NoArgsConstructor
public enum BaseEncoderImpl implements BaseEncoder{
    BASE_2(2, "01"),
    BASE_16 (16, "0123456789ABCDEF"),
    BASE_62 (62, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
    BASE_58 (58, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");

    private int base;
    private String characters;

    BaseEncoderImpl(int base, String characters) {
        this.base = base;
        this.characters = characters;
    }


    public List<String> encodeList(List<Long> numbers) {
        return numbers.stream()
                .map((number) -> encode(number))
                .toList();
    }
    @Override
    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }

    @Override
    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(BaseEncoderImpl.BASE_62.encode(123445));
    }
}
