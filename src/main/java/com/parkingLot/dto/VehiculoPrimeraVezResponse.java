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
public class VehiculoPrimeraVezResponse {
    private Long id;
    private String placa;
    private LocalDateTime fechaIngreso;
    private Boolean esPrimeraVez;
}
