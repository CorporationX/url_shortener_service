package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid format. Please use correct URL")
    @Size(max = 1024, message = "URL too long. Please use URL less than 1024 characters")
    private String url;
}
