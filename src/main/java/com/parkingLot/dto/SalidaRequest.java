package com.parkingLot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalidaRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotNull(message = "El ID del parqueadero es obligatorio")
    private Long parqueaderoId;
}
