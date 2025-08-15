package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UrlShortenerRandomBase62ServiceImpl implements UrlShortenerService {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateCode(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return stringBuilder.toString();
    }
}
