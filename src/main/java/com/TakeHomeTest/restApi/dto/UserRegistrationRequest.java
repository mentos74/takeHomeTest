package com.TakeHomeTest.restApi.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Email tidak boleh kosong")
    @Schema(example = "user@nutech-integrasi.com")
    @Email(message = "Parameter Email tidak sesuai format")

    private String email;


    @NotBlank(message = "Parameter first_name harus di isi")
    @JsonProperty("first_name")
    @Schema(example = "User")

    private String firstName;


    @NotBlank(message = "Parameter last_name harus di isi")
    @JsonProperty("last_name")
    @Schema(example = "Nutech")

    private String lastName;


    @NotBlank(message = "Parameter password harus di isi")
    @Schema(example = "abcdef1234")
    @Size(min = 8, message = "Password harus minimal 8 karakter")

    private String password;
}
