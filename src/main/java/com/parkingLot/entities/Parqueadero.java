package com.parkingLot.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parqueaderos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del parqueadero es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, length = 255)
    private String direccion;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @NotNull(message = "El costo por hora es obligatorio")
    @Min(value = 0, message = "El costo por hora debe ser mayor o igual a 0")
    @Column(name = "costo_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoPorHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "socio_id", nullable = false)
    @NotNull(message = "El socio es obligatorio")
    private User socio;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
