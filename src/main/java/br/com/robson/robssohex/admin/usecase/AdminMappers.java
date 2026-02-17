package br.com.robson.robssohex.admin.usecase;

import br.com.robson.robssohex.model.AdminPermissionResponse;
import br.com.robson.robssohex.model.AdminRoleResponse;
import br.com.robson.robssohex.model.AdminRoleSummary;
import br.com.robson.robssohex.model.AdminUserResponse;
import br.com.robson.robssohex.entities.Permission;
import br.com.robson.robssohex.entities.Role;
import br.com.robson.robssohex.entities.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class AdminMappers {

    private AdminMappers() {}

    public static AdminPermissionResponse toPermissionResponse(Permission permission) {
        return new AdminPermissionResponse()
                .id(permission.getId())
                .resource(permission.getResource())
                .action(permission.getAction());
    }

    public static AdminRoleResponse toRoleResponse(Role role) {
        List<AdminPermissionResponse> permissions = role.getPermissions().stream()
                .sorted(Comparator.comparing(Permission::getResource).thenComparing(Permission::getAction))
                .map(AdminMappers::toPermissionResponse)
                .collect(Collectors.toList());
        return new AdminRoleResponse()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .permissions(permissions);
    }

    public static AdminRoleSummary toRoleSummary(Role role) {
        return new AdminRoleSummary()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription());
    }

    public static AdminUserResponse toUserResponse(User user) {
        List<AdminRoleSummary> roles = user.getRoles().stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(AdminMappers::toRoleSummary)
                .collect(Collectors.toList());
        List<AdminPermissionResponse> permissions = user.getPermissions().stream()
                .sorted(Comparator.comparing(Permission::getResource).thenComparing(Permission::getAction))
                .map(AdminMappers::toPermissionResponse)
                .collect(Collectors.toList());
        return new AdminUserResponse()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .ativo(user.isAtivo())
            .roles(roles)
            .permissions(permissions);
    }
}
