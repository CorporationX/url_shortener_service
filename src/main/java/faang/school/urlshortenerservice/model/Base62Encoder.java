package faang.school.urlshortenerservice.model;

import java.util.ArrayList;
import java.util.List;

public class Base62Encoder {
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> numbers) {
        List<String> hashList = new ArrayList<>();
        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) == 0) {
                hashList.add(Character.toString(BASE62.charAt(0)));
            } else {
                long value = numbers.get(i);
                StringBuilder sb = new StringBuilder();
                while (value > 0) {
                    int remainder = (int) value % 62;

                }
            }
            long value = numbers.get(i);
            StringBuilder generatedHash = new StringBuilder();
            while (value > 0) {
                int remainder = (int) (value % 62);
                generatedHash.append(BASE62.charAt(remainder));
            }
            char[] charArray = numbers.get(i).toString().toCharArray();
            StringBuilder currentHash = new StringBuilder();
            for (int j = 0; j < charArray.length; j++) {
                currentHash.append(charArray[j]);
            }
        }
        return hashList;
    }

    public static void main(String[] args) {
        int value = (int) (100 % 62);
        System.out.println(1 / 62);
    }
}
