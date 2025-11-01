package com.parkingLot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParqueaderoResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidadMaxima;
    private BigDecimal costoPorHora;
    private SocioInfo socio;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SocioInfo {
        private Long id;
        private String nombre;
        private String email;
    }
}
