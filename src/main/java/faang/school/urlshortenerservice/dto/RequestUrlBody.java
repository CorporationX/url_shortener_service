package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUrlBody {

    @NotBlank(message = "URL must not be empty")
    @Pattern(
            regexp = "^(http?://)?(www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/[^\\s]*)?$",
            message = "Invalid URL format"
    )
    private String url;
}
