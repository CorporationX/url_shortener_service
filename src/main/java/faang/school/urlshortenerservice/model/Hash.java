package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash",  nullable = false)
    private String hash;
}