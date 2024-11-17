package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LongUrlDto {

    @NotEmpty
    @Pattern(regexp =
            "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/[a-zA-Z0-9-._~:/?#@!$&'()*+,;=]*)?$",
            message = "Wrong url format!")
    @Size(min = 5, max = 4096)
    private String url;
}
