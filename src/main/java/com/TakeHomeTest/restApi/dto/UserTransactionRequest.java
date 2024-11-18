package com.TakeHomeTest.restApi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserTransactionRequest {

    @NotNull(message = "Paramter service_code harus di isi")
    @JsonProperty("service_code")
    @Schema(example = "PULSA")
    private String serviceCode;
}
