package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlCreateDto {

    @URL
    @NotNull
    @NotEmpty
    private String url;
}