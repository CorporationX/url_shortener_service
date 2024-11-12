package faang.school.urlshortenerservice.model.dto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hash")
public class Hash {

    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @OneToOne(mappedBy = "hash")
    Url url;
}
