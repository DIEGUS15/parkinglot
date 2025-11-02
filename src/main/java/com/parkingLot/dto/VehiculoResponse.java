package com.parkingLot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponse {
    private Long id;
    private String placa;
    private LocalDateTime fechaIngreso;
    private Long parqueaderoId;
    private String parqueaderoNombre;
}
