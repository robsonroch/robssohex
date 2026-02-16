package br.com.robson.robssohex.transportlayers.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermissionRequest {
    private String resource;
    private String action;
}
