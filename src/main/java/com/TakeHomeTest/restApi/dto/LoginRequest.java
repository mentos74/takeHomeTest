package com.TakeHomeTest.restApi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {


    @Schema(example = "user@nutech-integrasi.com")
    @Email(message = "Parameter email tidak sesuai format")
    private String email;

    @Schema(example = "abcdef1234")
    @Size(min = 8, message = "Password harus minimal 8 karakter")
    private String password;
}
