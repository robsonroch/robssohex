package br.com.robson.robssohex.admin.service;

import br.com.robson.robssohex.entities.Permission;
import br.com.robson.robssohex.entities.Role;
import br.com.robson.robssohex.repositories.PermissionRepository;
import br.com.robson.robssohex.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleAdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Role create(String name, String description, Set<String> permissionIds) {
        validate(name);
        Role role = new Role();
        role.setName(name.trim());
        role.setDescription(description == null ? null : description.trim());
        role.setPermissions(resolvePermissions(permissionIds));
        return roleRepository.save(role);
    }

    public Page<Role> list(Pageable pageable, String search) {
        return roleRepository.search(search, pageable);
    }

    public Role getById(String id) {
        String safeId = Objects.requireNonNull(id, "id");
        return roleRepository.findById(safeId)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));
    }

    @Transactional
    public Role update(String id, String name, String description, Set<String> permissionIds) {
        String safeId = Objects.requireNonNull(id, "id");
        validate(name);
        Role role = getById(safeId);
        role.setName(name.trim());
        role.setDescription(description == null ? null : description.trim());
        role.setPermissions(resolvePermissions(permissionIds));
        return roleRepository.save(role);
    }

    @Transactional
    public void delete(String id) {
        String safeId = Objects.requireNonNull(id, "id");
        roleRepository.deleteById(safeId);
    }

    @Transactional
    public Role addPermission(String roleId, String permissionId) {
        String safeRoleId = Objects.requireNonNull(roleId, "roleId");
        String safePermissionId = Objects.requireNonNull(permissionId, "permissionId");
        Role role = getById(safeRoleId);
        Permission permission = permissionRepository.findById(safePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermission(String roleId, String permissionId) {
        String safeRoleId = Objects.requireNonNull(roleId, "roleId");
        String safePermissionId = Objects.requireNonNull(permissionId, "permissionId");
        Role role = getById(safeRoleId);
        Permission permission = permissionRepository.findById(safePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }

    private void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da role é obrigatório");
        }
    }

    private Set<Permission> resolvePermissions(Set<String> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new IllegalArgumentException("Uma ou mais permissões não foram encontradas");
        }
        return new HashSet<>(permissions);
    }
}
