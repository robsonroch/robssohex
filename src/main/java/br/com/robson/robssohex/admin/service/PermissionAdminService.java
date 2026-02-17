package br.com.robson.robssohex.admin.service;

import br.com.robson.robssohex.HttpMethodEnum;
import br.com.robson.robssohex.entities.Permission;
import br.com.robson.robssohex.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PermissionAdminService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public Permission create(String resource, String action) {
        validate(resource, action);
        Permission permission = new Permission();
        permission.setMethod(HttpMethodEnum.GET);
        permission.setResource(resource.trim());
        permission.setAction(action.trim());
        return permissionRepository.save(permission);
    }

    public Page<Permission> list(Pageable pageable, String search) {
        return permissionRepository.search(search, pageable);
    }

    public Permission getById(String id) {
        String safeId = Objects.requireNonNull(id, "id");
        return permissionRepository.findById(safeId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
    }

    @Transactional
    public Permission update(String id, String resource, String action) {
        String safeId = Objects.requireNonNull(id, "id");
        validate(resource, action);
        Permission permission = getById(safeId);
        permission.setResource(resource.trim());
        permission.setAction(action.trim());
        return permissionRepository.save(permission);
    }

    @Transactional
    public void delete(String id) {
        String safeId = Objects.requireNonNull(id, "id");
        permissionRepository.deleteById(safeId);
    }

    private void validate(String resource, String action) {
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource é obrigatório");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action é obrigatório");
        }
    }
}
