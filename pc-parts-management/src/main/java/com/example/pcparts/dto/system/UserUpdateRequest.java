package com.example.pcparts.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "username_required")
    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    @NotNull(message = "status_required")
    private Integer status;

    private List<Long> roleIds;
}
