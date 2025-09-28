package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Hash {

    @Id
    @Column(name = "hash", length = 6)
    @NotBlank
    private String hashValue;
}
