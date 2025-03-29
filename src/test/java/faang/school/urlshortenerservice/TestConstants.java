package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.UrlDto;

public class TestConstants {

    public static final long USER_ID = 1L;

    public static final String NON_EXISTENT_HASH = "000";

    public static final UrlDto CORRECT_URL_DTO = new UrlDto("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    public static final UrlDto INVALID_URL_DTO = new UrlDto("www.youtube.com/watch?v=dQw4w9WgXcQ");
    public static final UrlDto EMPTY_URL_DTO = new UrlDto("");

    public static String getHashFromShortLink(String shortLink) {
        int lastSlashIndex = shortLink.lastIndexOf('/');
        return shortLink.substring(lastSlashIndex + 1);
    }
}
