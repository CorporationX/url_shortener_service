package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseConversion {

    @Value("${app.hash-generator.base}}")
    private int base;
    @Value("${app.hash-generator.characters}}")
    private String characters;

    //TODO: я сделал так, что бы в ямле можно было выбирать формат кодирования.
    // Есть вариант сделать стоические поля и использовать их классы для выбора
    // формата кодирования и декодирования
   /* Типа так сделать, и создать можно отдельный конфигурационный класс =)
    public final static BaseEncoder BASE_2 = new Base10Encoder(2, "01");
    public final static BaseEncoder BASE_8 = new Base10Encoder(8, "01234567");
    public final static BaseEncoder BASE_10 = new Base10Encoder(10, "0123456789");
    public final static BaseEncoder BASE_16 = new Base10Encoder(16, "0123456789ABCDEF");
    public final static BaseEncoder BASE_18 = new Base10Encoder(18, "0123456789ABCDEFGH");
    public final static BaseEncoder BASE_32 = new Base10Encoder(32, "0123456789ABCDEFGHIJKLMNOPQRSTUV");
    public final static BaseEncoder BASE_58 = new Base10Encoder(58, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
    public final static BaseEncoder BASE_62 = new Base10Encoder(62, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    */

    public String encodeSingleNumber (long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeSingleNumber)
                .toList();
    }

    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }

}