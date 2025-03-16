package faang.school.urlshortenerservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InsertDataDto {
    private List<@NonNull String> hashes;
}
