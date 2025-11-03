package com.parkingLot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GananciasResponse {
    private String periodo;
    private BigDecimal totalGanancias;
    private Long cantidadVehiculos;
    private Long parqueaderoId;
    private String parqueaderoNombre;
}
