package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "hash")
@AllArgsConstructor
@Builder
public class Hash {

    @Column(name = "hash", unique = true)
    private String hash;
}
