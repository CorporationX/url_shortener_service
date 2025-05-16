package faang.school.urlshortenerservice.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Url {
    private String hash;
    private String url;
    private Timestamp createdAt;
}