package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUrlDto {

    @NotNull(message = "Url shouldn't be a null")
    @URL(message = "Url format incorrect")
    @Length(max = 255, message = "Maximum numbers of characters 255")
    private String url;
}