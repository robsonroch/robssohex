package br.com.robson.robssohex.admin.usecase;

import br.com.robson.robssohex.model.AdminPagedResponse;
import br.com.robson.robssohex.model.AdminPermissionRequest;
import br.com.robson.robssohex.model.AdminPermissionResponse;
import br.com.robson.robssohex.admin.service.PermissionAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionAdminUseCase {

    private final PermissionAdminService permissionAdminService;

    public AdminPermissionResponse create(AdminPermissionRequest request) {
        var created = permissionAdminService.create(request.getResource(), request.getAction());
        return AdminMappers.toPermissionResponse(created);
    }

        public AdminPagedResponse list(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminPermissionResponse> pageData = permissionAdminService.list(pageable, search)
            .map(AdminMappers::toPermissionResponse);
        return new AdminPagedResponse()
            .content(List.copyOf(pageData.getContent()))
            .page(pageData.getNumber())
            .size(pageData.getSize())
            .totalElements(pageData.getTotalElements())
            .totalPages(pageData.getTotalPages());
    }

    public AdminPermissionResponse getById(String id) {
        return AdminMappers.toPermissionResponse(permissionAdminService.getById(id));
    }

    public AdminPermissionResponse update(String id, AdminPermissionRequest request) {
        var updated = permissionAdminService.update(id, request.getResource(), request.getAction());
        return AdminMappers.toPermissionResponse(updated);
    }

    public void delete(String id) {
        permissionAdminService.delete(id);
    }
}
