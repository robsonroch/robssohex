package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, String> {

	@Query("select r from Role r " +
			"where (:search is null or :search = '' " +
			"or lower(r.name) like lower(concat('%', :search, '%')))")
	Page<Role> search(@Param("search") String search, Pageable pageable);
}