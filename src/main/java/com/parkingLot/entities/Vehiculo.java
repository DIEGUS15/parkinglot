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
@Table(name = "vehiculos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

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

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
    }
}
