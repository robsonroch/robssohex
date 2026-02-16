package br.com.robson.robssohex.admin.controller;

import br.com.robson.robssohex.admin.usecase.PermissionAdminUseCase;
import br.com.robson.robssohex.admin.usecase.RoleAdminUseCase;
import br.com.robson.robssohex.admin.usecase.UserAdminUseCase;
import br.com.robson.robssohex.api.AdminApi;
import br.com.robson.robssohex.model.AdminPagedResponse;
import br.com.robson.robssohex.model.AdminPermissionRequest;
import br.com.robson.robssohex.model.AdminPermissionResponse;
import br.com.robson.robssohex.model.AdminRoleRequest;
import br.com.robson.robssohex.model.AdminRoleResponse;
import br.com.robson.robssohex.model.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminApiImpl implements AdminApi {

    private final PermissionAdminUseCase permissionAdminUseCase;
    private final RoleAdminUseCase roleAdminUseCase;
    private final UserAdminUseCase userAdminUseCase;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdminUserResponse> adminMe() {
        return ResponseEntity.ok(userAdminUseCase.me());
    }

    @Override
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<AdminPagedResponse> listPermissions(Integer page, Integer size, String search) {
        int pageValue = page == null ? 0 : page;
        int sizeValue = size == null ? 10 : size;
        return ResponseEntity.ok(permissionAdminUseCase.list(pageValue, sizeValue, search));
    }

    @Override
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<AdminPermissionResponse> createPermission(AdminPermissionRequest adminPermissionRequest) {
        return ResponseEntity.ok(permissionAdminUseCase.create(adminPermissionRequest));
    }

    @Override
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<AdminPermissionResponse> getPermission(String id) {
        return ResponseEntity.ok(permissionAdminUseCase.getById(id));
    }

    @Override
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<AdminPermissionResponse> updatePermission(String id, AdminPermissionRequest adminPermissionRequest) {
        return ResponseEntity.ok(permissionAdminUseCase.update(id, adminPermissionRequest));
    }

    @Override
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<Void> deletePermission(String id) {
        permissionAdminUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminPagedResponse> listRoles(Integer page, Integer size, String search) {
        int pageValue = page == null ? 0 : page;
        int sizeValue = size == null ? 10 : size;
        return ResponseEntity.ok(roleAdminUseCase.list(pageValue, sizeValue, search));
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminRoleResponse> createRole(AdminRoleRequest adminRoleRequest) {
        return ResponseEntity.ok(roleAdminUseCase.create(adminRoleRequest));
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminRoleResponse> getRole(String id) {
        return ResponseEntity.ok(roleAdminUseCase.getById(id));
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminRoleResponse> updateRole(String id, AdminRoleRequest adminRoleRequest) {
        return ResponseEntity.ok(roleAdminUseCase.update(id, adminRoleRequest));
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<Void> deleteRole(String id) {
        roleAdminUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminRoleResponse> addPermissionToRole(String roleId, String permissionId) {
        return ResponseEntity.ok(roleAdminUseCase.addPermission(roleId, permissionId));
    }

    @Override
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<AdminRoleResponse> removePermissionFromRole(String roleId, String permissionId) {
        return ResponseEntity.ok(roleAdminUseCase.removePermission(roleId, permissionId));
    }

    @Override
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<AdminPagedResponse> listUsers(Integer page, Integer size, String search, Boolean active) {
        int pageValue = page == null ? 0 : page;
        int sizeValue = size == null ? 10 : size;
        return ResponseEntity.ok(userAdminUseCase.list(pageValue, sizeValue, search, active));
    }

    @Override
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<AdminUserResponse> getUser(String id) {
        return ResponseEntity.ok(userAdminUseCase.getById(id));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> activateUser(String id) {
        return ResponseEntity.ok(userAdminUseCase.activate(id));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> deactivateUser(String id) {
        return ResponseEntity.ok(userAdminUseCase.deactivate(id));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> addRoleToUser(String userId, String roleId) {
        return ResponseEntity.ok(userAdminUseCase.addRole(userId, roleId));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> removeRoleFromUser(String userId, String roleId) {
        return ResponseEntity.ok(userAdminUseCase.removeRole(userId, roleId));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> addPermissionToUser(String userId, String permissionId) {
        return ResponseEntity.ok(userAdminUseCase.addPermission(userId, permissionId));
    }

    @Override
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AdminUserResponse> removePermissionFromUser(String userId, String permissionId) {
        return ResponseEntity.ok(userAdminUseCase.removePermission(userId, permissionId));
    }
}
