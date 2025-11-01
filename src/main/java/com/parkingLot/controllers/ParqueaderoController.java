package com.parkingLot.controllers;

import com.parkingLot.dto.ParqueaderoRequest;
import com.parkingLot.dto.ParqueaderoResponse;
import com.parkingLot.services.ParqueaderoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parqueaderos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede acceder a todos los endpoints
public class ParqueaderoController {

    private final ParqueaderoService parqueaderoService;

    @PostMapping
    public ResponseEntity<ParqueaderoResponse> crear(@Valid @RequestBody ParqueaderoRequest request) {
        ParqueaderoResponse response = parqueaderoService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParqueaderoResponse>> listarTodos() {
        List<ParqueaderoResponse> parqueaderos = parqueaderoService.listarTodos();
        return ResponseEntity.ok(parqueaderos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ParqueaderoResponse>> listarActivos() {
        List<ParqueaderoResponse> parqueaderos = parqueaderoService.listarActivos();
        return ResponseEntity.ok(parqueaderos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParqueaderoResponse> obtenerPorId(@PathVariable Long id) {
        ParqueaderoResponse response = parqueaderoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<ParqueaderoResponse>> listarPorSocio(@PathVariable Long socioId) {
        List<ParqueaderoResponse> parqueaderos = parqueaderoService.listarPorSocio(socioId);
        return ResponseEntity.ok(parqueaderos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParqueaderoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ParqueaderoRequest request) {
        ParqueaderoResponse response = parqueaderoService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        parqueaderoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Void> eliminarDefinitivamente(@PathVariable Long id) {
        parqueaderoService.eliminarDefinitivamente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ParqueaderoResponse> activar(@PathVariable Long id) {
        ParqueaderoResponse response = parqueaderoService.activar(id);
        return ResponseEntity.ok(response);
    }
}
