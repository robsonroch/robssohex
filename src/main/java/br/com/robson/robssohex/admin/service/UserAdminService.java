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

import java.util.Objects;

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
    public User removePermission(String userId, String permissionId) {
        String safeUserId = Objects.requireNonNull(userId, "userId");
        String safePermissionId = Objects.requireNonNull(permissionId, "permissionId");
        User user = getById(safeUserId);
        Permission permission = permissionRepository.findById(safePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada"));
        user.getPermissions().remove(permission);
        return userRepository.save(user);
    }
}
