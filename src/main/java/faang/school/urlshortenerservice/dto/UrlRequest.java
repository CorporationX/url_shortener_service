package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @URL(message = "Must be valid URL")
    private String url;
    @Future(message = "Must be future date")
    private LocalDateTime expireAt;
}
