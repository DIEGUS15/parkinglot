package com.parkingLot.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos_historial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La placa es obligatoria")
    @Column(nullable = false, length = 20)
    private String placa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    @NotNull(message = "El parqueadero es obligatorio")
    private Parqueadero parqueadero;

    @Column(name = "fecha_ingreso", nullable = false)
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida", nullable = false)
    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime fechaSalida;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
