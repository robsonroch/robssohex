package br.com.robson.robssohex.repositories;

import br.com.robson.robssohex.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

	@Query("select p from Permission p " +
			"where (:search is null or :search = '' " +
			"or lower(p.resource) like lower(concat('%', :search, '%')) " +
			"or lower(p.action) like lower(concat('%', :search, '%')))")
	Page<Permission> search(@Param("search") String search, Pageable pageable);
}