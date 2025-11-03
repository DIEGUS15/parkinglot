package com.parkingLot.controllers;

import com.parkingLot.dto.GananciasResponse;
import com.parkingLot.dto.IngresoRequest;
import com.parkingLot.dto.IngresoResponse;
import com.parkingLot.dto.SalidaRequest;
import com.parkingLot.dto.SalidaResponse;
import com.parkingLot.dto.TopVehiculoResponse;
import com.parkingLot.dto.VehiculoPrimeraVezResponse;
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

    @GetMapping("/top-10-frecuentes")
    public ResponseEntity<List<TopVehiculoResponse>> obtenerTop10VehiculosMasFrecuentes() {
        List<TopVehiculoResponse> topVehiculos = vehiculoService.obtenerTop10VehiculosMasFrecuentes();
        return ResponseEntity.ok(topVehiculos);
    }

    @GetMapping("/top-10-frecuentes/parqueadero/{parqueaderoId}")
    public ResponseEntity<List<TopVehiculoResponse>> obtenerTop10VehiculosMasFrecuentesPorParqueadero(@PathVariable Long parqueaderoId) {
        List<TopVehiculoResponse> topVehiculos = vehiculoService.obtenerTop10VehiculosMasFrecuentesPorParqueadero(parqueaderoId);
        return ResponseEntity.ok(topVehiculos);
    }

    @GetMapping("/primera-vez/parqueadero/{parqueaderoId}")
    public ResponseEntity<List<VehiculoPrimeraVezResponse>> obtenerVehiculosPrimeraVezEnParqueadero(@PathVariable Long parqueaderoId) {
        List<VehiculoPrimeraVezResponse> vehiculosPrimeraVez = vehiculoService.obtenerVehiculosPrimeraVezEnParqueadero(parqueaderoId);
        return ResponseEntity.ok(vehiculosPrimeraVez);
    }

    @GetMapping("/ganancias/hoy/parqueadero/{parqueaderoId}")
    public ResponseEntity<GananciasResponse> obtenerGananciasHoy(@PathVariable Long parqueaderoId) {
        GananciasResponse ganancias = vehiculoService.obtenerGananciasHoy(parqueaderoId);
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/ganancias/semana/parqueadero/{parqueaderoId}")
    public ResponseEntity<GananciasResponse> obtenerGananciasSemana(@PathVariable Long parqueaderoId) {
        GananciasResponse ganancias = vehiculoService.obtenerGananciasSemana(parqueaderoId);
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/ganancias/mes/parqueadero/{parqueaderoId}")
    public ResponseEntity<GananciasResponse> obtenerGananciasMes(@PathVariable Long parqueaderoId) {
        GananciasResponse ganancias = vehiculoService.obtenerGananciasMes(parqueaderoId);
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/ganancias/anio/parqueadero/{parqueaderoId}")
    public ResponseEntity<GananciasResponse> obtenerGananciasAnio(@PathVariable Long parqueaderoId) {
        GananciasResponse ganancias = vehiculoService.obtenerGananciasAnio(parqueaderoId);
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<VehiculoResponse>> buscarVehiculosPorPlaca(@RequestParam String placa) {
        List<VehiculoResponse> vehiculos = vehiculoService.buscarVehiculosPorPlaca(placa);
        return ResponseEntity.ok(vehiculos);
    }
}
