package com.test.aegis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleData {

    private Long id;

    @NotBlank(message = "Name is required and cannot be blank")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public RoleData(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
