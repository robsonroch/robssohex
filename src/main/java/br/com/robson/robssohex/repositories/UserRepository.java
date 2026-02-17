package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByAtivoTrue();
    boolean existsByEmailAndAtivoTrue(String email);

    @Query("select u from User u " +
            "where (:active is null or u.ativo = :active) " +
            "and (:search is null or :search = '' " +
            "or lower(u.username) like lower(concat('%', :search, '%')) " +
            "or lower(u.email) like lower(concat('%', :search, '%')))")
    Page<User> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);
}