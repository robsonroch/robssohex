package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    List<User> findAllByAtivoTrue();
    boolean existsByEmailAndAtivoTrue(String email);
}