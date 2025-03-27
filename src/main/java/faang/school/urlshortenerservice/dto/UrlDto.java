package faang.school.urlshortenerservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {

    @NotBlank
    @Length(max = 255)
    @URL
    private String url;

}
