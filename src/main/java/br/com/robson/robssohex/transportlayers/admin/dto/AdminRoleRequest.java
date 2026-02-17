package br.com.robson.robssohex.transportlayers.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleRequest {
    private String name;
    private String description;
    private Set<String> permissionIds;
}
