package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUrlDto { //TODO возможно одного дто хватит если будет только одно значние в нём

    private String shortUrl;
}
