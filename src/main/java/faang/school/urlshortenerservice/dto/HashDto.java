package faang.school.urlshortenerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HashDto {
    private String hash;
}