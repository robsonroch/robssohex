package br.com.robson.robssohex.admin.usecase;

import br.com.robson.robssohex.model.AdminPagedResponse;
import br.com.robson.robssohex.model.AdminUserResponse;
import br.com.robson.robssohex.model.BatchUpdateUserPermissionsRequest;
import br.com.robson.robssohex.model.BatchUpdateUserRolesRequest;
import br.com.robson.robssohex.model.ReplaceUserAccessRequest;
import br.com.robson.robssohex.model.ReplaceUserPermissionsRequest;
import br.com.robson.robssohex.model.ReplaceUserRolesRequest;
import br.com.robson.robssohex.admin.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAdminUseCase {

    private final UserAdminService userAdminService;

    public AdminUserResponse me() {
        return AdminMappers.toUserResponse(userAdminService.getAuthenticatedUser());
    }

        public AdminPagedResponse list(int page, int size, String search, Boolean active) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> pageData = userAdminService.list(pageable, search, active)
            .map(AdminMappers::toUserResponse);
        return new AdminPagedResponse()
            .content(List.copyOf(pageData.getContent()))
            .page(pageData.getNumber())
            .size(pageData.getSize())
            .totalElements(pageData.getTotalElements())
            .totalPages(pageData.getTotalPages());
    }

    public AdminUserResponse getById(String id) {
        return AdminMappers.toUserResponse(userAdminService.getById(id));
    }

    public AdminUserResponse activate(String id) {
        return AdminMappers.toUserResponse(userAdminService.activate(id));
    }

    public AdminUserResponse deactivate(String id) {
        return AdminMappers.toUserResponse(userAdminService.deactivate(id));
    }

    public AdminUserResponse addRole(String userId, String roleId) {
        return AdminMappers.toUserResponse(userAdminService.addRole(userId, roleId));
    }

    public AdminUserResponse removeRole(String userId, String roleId) {
        return AdminMappers.toUserResponse(userAdminService.removeRole(userId, roleId));
    }

    public AdminUserResponse addPermission(String userId, String permissionId) {
        return AdminMappers.toUserResponse(userAdminService.addPermission(userId, permissionId));
    }

    public AdminUserResponse removePermission(String userId, String permissionId) {
        return AdminMappers.toUserResponse(userAdminService.removePermission(userId, permissionId));
    }

    public AdminUserResponse batchUpdateRoles(String userId, BatchUpdateUserRolesRequest request) {
        Set<String> addRoleIds = toIdSet(request == null ? null : request.getAddRoleIds());
        Set<String> removeRoleIds = toIdSet(request == null ? null : request.getRemoveRoleIds());
        return AdminMappers.toUserResponse(userAdminService.batchUpdateRoles(userId, addRoleIds, removeRoleIds));
    }

    public AdminUserResponse batchUpdatePermissions(String userId, BatchUpdateUserPermissionsRequest request) {
        Set<String> addPermissionIds = toIdSet(request == null ? null : request.getAddPermissionIds());
        Set<String> removePermissionIds = toIdSet(request == null ? null : request.getRemovePermissionIds());
        return AdminMappers.toUserResponse(userAdminService.batchUpdatePermissions(userId, addPermissionIds, removePermissionIds));
    }

    public AdminUserResponse replaceRoles(String userId, ReplaceUserRolesRequest request) {
        Set<String> roleIds = toIdSet(request == null ? null : request.getRoleIds());
        return AdminMappers.toUserResponse(userAdminService.replaceRoles(userId, roleIds));
    }

    public AdminUserResponse replacePermissions(String userId, ReplaceUserPermissionsRequest request) {
        Set<String> permissionIds = toIdSet(request == null ? null : request.getPermissionIds());
        return AdminMappers.toUserResponse(userAdminService.replacePermissions(userId, permissionIds));
    }

    public AdminUserResponse replaceAccess(String userId, ReplaceUserAccessRequest request) {
        Set<String> roleIds = toIdSet(request == null ? null : request.getRoleIds());
        Set<String> permissionIds = toIdSet(request == null ? null : request.getPermissionIds());
        return AdminMappers.toUserResponse(userAdminService.replaceAccess(userId, roleIds, permissionIds));
    }

    private Set<String> toIdSet(List<String> ids) {
        if (ids == null) {
            return Set.of();
        }
        return ids.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}
