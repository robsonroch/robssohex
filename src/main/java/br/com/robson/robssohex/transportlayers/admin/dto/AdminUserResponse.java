package br.com.robson.robssohex.transportlayers.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private String id;
    private String username;
    private String email;
    private boolean ativo;
    private List<AdminRoleSummary> roles;
    private List<AdminPermissionResponse> permissions;
}
