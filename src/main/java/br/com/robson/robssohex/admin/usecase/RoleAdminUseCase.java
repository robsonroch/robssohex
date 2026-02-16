package br.com.robson.robssohex.admin.usecase;

import br.com.robson.robssohex.model.AdminPagedResponse;
import br.com.robson.robssohex.model.AdminRoleRequest;
import br.com.robson.robssohex.model.AdminRoleResponse;
import br.com.robson.robssohex.admin.service.RoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleAdminUseCase {

    private final RoleAdminService roleAdminService;

    public AdminRoleResponse create(AdminRoleRequest request) {
        var created = roleAdminService.create(
                request.getName(),
                request.getDescription(),
                request.getPermissionIds() == null ? null : new HashSet<>(request.getPermissionIds())
        );
        return AdminMappers.toRoleResponse(created);
    }

        public AdminPagedResponse list(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminRoleResponse> pageData = roleAdminService.list(pageable, search)
            .map(AdminMappers::toRoleResponse);
        return new AdminPagedResponse()
            .content(List.copyOf(pageData.getContent()))
            .page(pageData.getNumber())
            .size(pageData.getSize())
            .totalElements(pageData.getTotalElements())
            .totalPages(pageData.getTotalPages());
    }

    public AdminRoleResponse getById(String id) {
        return AdminMappers.toRoleResponse(roleAdminService.getById(id));
    }

    public AdminRoleResponse update(String id, AdminRoleRequest request) {
        var updated = roleAdminService.update(
                id,
                request.getName(),
                request.getDescription(),
                request.getPermissionIds() == null ? null : new HashSet<>(request.getPermissionIds())
        );
        return AdminMappers.toRoleResponse(updated);
    }

    public void delete(String id) {
        roleAdminService.delete(id);
    }

    public AdminRoleResponse addPermission(String roleId, String permissionId) {
        return AdminMappers.toRoleResponse(roleAdminService.addPermission(roleId, permissionId));
    }

    public AdminRoleResponse removePermission(String roleId, String permissionId) {
        return AdminMappers.toRoleResponse(roleAdminService.removePermission(roleId, permissionId));
    }
}
