package br.com.robson.robssohex;

import br.com.robson.robssohex.entities.Permission;
import br.com.robson.robssohex.entities.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserClaims {

    private String username;

    private String email;

    private Set<Role> roles = new HashSet<>();

    private Set<Permission> permissions = new HashSet<>();
}