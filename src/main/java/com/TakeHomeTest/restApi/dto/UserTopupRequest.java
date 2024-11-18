package com.TakeHomeTest.restApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserTopupRequest {

    @NotNull(message = "Paramter top_up_amount harus di isi")
    @JsonProperty("top_up_amount")
    @Schema(example = "0")
    @PositiveOrZero(message = "Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0")
    private BigDecimal topUpAmount;

}
