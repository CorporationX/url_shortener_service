package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @Size(min = 6, max = 6)
    @NotBlank(message = "Hash cannot be blank")
    private String hash;
}
