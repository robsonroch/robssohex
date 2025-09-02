package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}