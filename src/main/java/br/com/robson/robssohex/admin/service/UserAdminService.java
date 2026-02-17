package br.com.robson.robssohex.admin.service;

import br.com.robson.robssohex.entities.Permission;
import br.com.robson.robssohex.entities.Role;
import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.repositories.PermissionRepository;
import br.com.robson.robssohex.repositories.RoleRepository;
import br.com.robson.robssohex.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Page<User> list(Pageable pageable, String search, Boolean active) {
        return userRepository.search(search, active, pageable);
    }

    public User getById(String id) {
        String safeId = Objects.requireNonNull(id, "id");
        return userRepository.findById(safeId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }
        Object details = authentication.getDetails();
        if (details instanceof String email && !email.isBlank()) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    @Transactional
    public User activate(String id) {
        User user = getById(Objects.requireNonNull(id, "id"));
        user.setAtivo(true);
        return userRepository.save(user);
    }

    @Transactional
    public User deactivate(String id) {
        User user = getById(Objects.requireNonNull(id, "id"));
        user.setAtivo(false);
        return userRepository.save(user);
    }

    @Transactional
    public User addRole(String userId, String roleId) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        String safeRoleId = Objects.requireNonNull(roleId, "roleId");
        User user = getById(safeUserId);
        Role role = roleRepository.findById(safeRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public User batchUpdateRoles(String userId, Set<String> addRoleIds, Set<String> removeRoleIds) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        User user = getById(safeUserId);
        Set<Role> rolesToAdd = findRolesOrThrow(addRoleIds, "Role não encontrada");
        Set<Role> rolesToRemove = findRolesOrThrow(removeRoleIds, "Role não encontrada");
        user.getRoles().addAll(rolesToAdd);
        user.getRoles().removeAll(rolesToRemove);
        return userRepository.save(user);
    }

    @Transactional
    public User replaceRoles(String userId, Set<String> roleIds) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        User user = getById(safeUserId);
        Set<Role> roles = findRolesOrThrow(roleIds, "Role não encontrada");
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRole(String userId, String roleId) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        String safeRoleId = Objects.requireNonNull(roleId, "roleId");
        User user = getById(safeUserId);
        Role role = roleRepository.findById(safeRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Transactional
    public User addPermission(String userId, String permissionId) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        String safePermissionId = Objects.requireNonNull(permissionId, "permissionId");
        User user = getById(safeUserId);
        Permission permission = permissionRepository.findById(safePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
        user.getPermissions().add(permission);
        return userRepository.save(user);
    }

    @Transactional
    public User batchUpdatePermissions(String userId, Set<String> addPermissionIds, Set<String> removePermissionIds) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        User user = getById(safeUserId);
        Set<Permission> permissionsToAdd = findPermissionsOrThrow(addPermissionIds, "Permissão não encontrada");
        Set<Permission> permissionsToRemove = findPermissionsOrThrow(removePermissionIds, "Permissão não encontrada");
        user.getPermissions().addAll(permissionsToAdd);
        user.getPermissions().removeAll(permissionsToRemove);
        return userRepository.save(user);
    }

    @Transactional
    public User replacePermissions(String userId, Set<String> permissionIds) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        User user = getById(safeUserId);
        Set<Permission> permissions = findPermissionsOrThrow(permissionIds, "Permissão não encontrada");
        user.getPermissions().clear();
        user.getPermissions().addAll(permissions);
        return userRepository.save(user);
    }

    @Transactional
    public User removePermission(String userId, String permissionId) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        String safePermissionId = Objects.requireNonNull(permissionId, "permissionId");
        User user = getById(safeUserId);
        Permission permission = permissionRepository.findById(safePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
        user.getPermissions().remove(permission);
        return userRepository.save(user);
    }

    @Transactional
    public User replaceAccess(String userId, Set<String> roleIds, Set<String> permissionIds) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        User user = getById(safeUserId);
        Set<Role> roles = findRolesOrThrow(roleIds, "Role não encontrada");
        Set<Permission> permissions = findPermissionsOrThrow(permissionIds, "Permissão não encontrada");
        user.getRoles().clear();
        user.getPermissions().clear();
        user.getRoles().addAll(roles);
        user.getPermissions().addAll(permissions);
        return userRepository.save(user);
    }

    private Set<Role> findRolesOrThrow(Set<String> ids, String errorMessage) {
        Set<String> safeIds = normalizeIds(ids);
        if (safeIds.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(safeIds));
        validateAllFound(safeIds, roles.stream().map(Role::getId).collect(Collectors.toSet()), errorMessage);
        return roles;
    }

    private Set<Permission> findPermissionsOrThrow(Set<String> ids, String errorMessage) {
        Set<String> safeIds = normalizeIds(ids);
        if (safeIds.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(safeIds));
        validateAllFound(safeIds, permissions.stream().map(Permission::getId).collect(Collectors.toSet()), errorMessage);
        return permissions;
    }

    private Set<String> normalizeIds(Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private void validateAllFound(Set<String> expectedIds, Set<String> foundIds, String errorMessage) {
        if (!foundIds.containsAll(expectedIds)) {
            Set<String> missing = expectedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw new IllegalArgumentException(errorMessage + ": " + String.join(", ", missing));
        }
    }
}
