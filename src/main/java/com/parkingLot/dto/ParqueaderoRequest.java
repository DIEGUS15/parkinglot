package com.parkingLot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParqueaderoRequest {

    @NotBlank(message = "El nombre del parqueadero es obligatorio")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidadMaxima;

    @NotNull(message = "El costo por hora es obligatorio")
    @Min(value = 0, message = "El costo por hora debe ser mayor o igual a 0")
    private BigDecimal costoPorHora;

    @NotNull(message = "El ID del socio es obligatorio")
    private Long socioId;
}
