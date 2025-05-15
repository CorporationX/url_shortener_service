package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.enity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuthDto {
    private String username;
    private String password;
    private Set<Role> roles;
}
