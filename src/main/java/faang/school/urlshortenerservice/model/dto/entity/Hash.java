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

    //TODO need to think about this field
    @OneToOne(mappedBy = "hash")
    Url url;

    public Hash(String hash) {
        this.hash = hash;
    }
}
