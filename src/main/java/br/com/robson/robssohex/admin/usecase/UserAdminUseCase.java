package br.com.robson.robssohex.admin.usecase;

import br.com.robson.robssohex.model.AdminPagedResponse;
import br.com.robson.robssohex.model.AdminUserResponse;
import br.com.robson.robssohex.admin.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
