package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

import java.time.LocalDateTime;

public class IntegrationTestConstants {

    public static final UrlDto CORRECT_URL = UrlDto.builder()
            .url("https://www.youtube.com/watch?v=ZmKy_fnRM_E&t=2s")
            .build();

    public static final UrlDto INVALID_URL = UrlDto.builder()
            .url("www.invalidUrl.com")
            .build();

    public static final UrlDto EMPTY_URL = UrlDto.builder()
            .url("")
            .build();

    public static final long USER_ID = 100;

    public static final String EXISTING_HASH = "x0";

    public static final String NON_EXISTING_HASH = "NOT_EXIST";

    public static final Url URL = Url.builder()
            .url(CORRECT_URL.url())
            .hash(EXISTING_HASH)
            .build();

    public static final Url EXPIRED_URL_1 = Url.builder()
            .hash("old1")
            .url(CORRECT_URL.url())
            .build();

    public static final Url EXPIRED_URL_2 = Url.builder()
            .hash("old2")
            .url(CORRECT_URL.url())
            .build();
}
