package faang.school.urlshortenerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @URL(protocol = "https", message = "Некорректный URL")
    private String url;
}
