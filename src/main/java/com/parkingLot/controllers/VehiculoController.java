package com.parkingLot.controllers;

import com.parkingLot.dto.IngresoRequest;
import com.parkingLot.dto.IngresoResponse;
import com.parkingLot.dto.SalidaRequest;
import com.parkingLot.dto.SalidaResponse;
import com.parkingLot.dto.VehiculoResponse;
import com.parkingLot.services.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping("/registrar-ingreso")
    public ResponseEntity<IngresoResponse> registrarIngreso(@Valid @RequestBody IngresoRequest request) {
        IngresoResponse response = vehiculoService.registrarIngreso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/registrar-salida")
    public ResponseEntity<SalidaResponse> registrarSalida(@Valid @RequestBody SalidaRequest request) {
        SalidaResponse response = vehiculoService.registrarSalida(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parqueadero/{parqueaderoId}")
    public ResponseEntity<List<VehiculoResponse>> listarVehiculosEnParqueadero(@PathVariable Long parqueaderoId) {
        List<VehiculoResponse> vehiculos = vehiculoService.listarVehiculosEnParqueadero(parqueaderoId);
        return ResponseEntity.ok(vehiculos);
    }
}
