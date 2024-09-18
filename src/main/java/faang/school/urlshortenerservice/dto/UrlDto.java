package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlDto {

    @Pattern(
            regexp = "^(https://|http://)([\\w.-]+)+(:\\d+)?(/\\S+)?$",
            message = "The URL must be correct"
    )
    private String url;

    private int hour;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
