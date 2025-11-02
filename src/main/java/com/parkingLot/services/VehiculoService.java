package com.parkingLot.services;

import com.parkingLot.dto.IngresoRequest;
import com.parkingLot.dto.IngresoResponse;
import com.parkingLot.dto.SalidaRequest;
import com.parkingLot.dto.SalidaResponse;
import com.parkingLot.dto.VehiculoResponse;
import com.parkingLot.entities.Parqueadero;
import com.parkingLot.entities.Vehiculo;
import com.parkingLot.entities.VehiculoHistorial;
import com.parkingLot.exceptions.BadRequestException;
import com.parkingLot.repositories.ParqueaderoRepository;
import com.parkingLot.repositories.VehiculoHistorialRepository;
import com.parkingLot.repositories.VehiculoRepository;
import com.parkingLot.utils.PlacaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ParqueaderoRepository parqueaderoRepository;
    private final VehiculoHistorialRepository vehiculoHistorialRepository;

    @Transactional
    public IngresoResponse registrarIngreso(IngresoRequest request) {
        log.info("Registrando ingreso de vehículo con placa: {} al parqueadero ID: {}",
                request.getPlaca(), request.getParqueaderoId());

        String placaNormalizada = PlacaValidator.normalizarYValidarPlaca(request.getPlaca());

        if (vehiculoRepository.existsByPlacaAndFechaSalidaIsNullAndActivoTrue(placaNormalizada)) {
            log.warn("Intento de registrar placa {} que ya está en un parqueadero", placaNormalizada);
            throw new BadRequestException(
                    "No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero");
        }

        Parqueadero parqueadero = parqueaderoRepository.findById(request.getParqueaderoId())
                .orElseThrow(() -> new BadRequestException(
                        "El parqueadero con ID " + request.getParqueaderoId() + " no existe"));

        if (!parqueadero.getActivo()) {
            throw new BadRequestException("El parqueadero no está activo");
        }

        long vehiculosActuales = vehiculoRepository
                .countByParqueaderoIdAndFechaSalidaIsNull(request.getParqueaderoId());
        if (vehiculosActuales >= parqueadero.getCapacidadMaxima()) {
            log.warn("Intento de ingresar al parqueadero ID: {} que está lleno ({}/{})",
                    request.getParqueaderoId(), vehiculosActuales, parqueadero.getCapacidadMaxima());
            throw new BadRequestException(
                    "No se puede Registrar Ingreso, el parqueadero ha alcanzado su capacidad máxima (" +
                            vehiculosActuales + "/" + parqueadero.getCapacidadMaxima() + ")");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placaNormalizada)
                .parqueadero(parqueadero)
                .fechaIngreso(LocalDateTime.now())
                .activo(true)
                .build();

        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        log.info("Ingreso registrado exitosamente con ID: {} (Ocupación: {}/{})",
                vehiculoGuardado.getId(), vehiculosActuales + 1, parqueadero.getCapacidadMaxima());

        return IngresoResponse.builder()
                .id(vehiculoGuardado.getId())
                .build();
    }

    @Transactional
    public SalidaResponse registrarSalida(SalidaRequest request) {
        log.info("Registrando salida de vehículo con placa: {} del parqueadero ID: {}",
                request.getPlaca(), request.getParqueaderoId());

        String placaNormalizada = PlacaValidator.normalizarYValidarPlaca(request.getPlaca());

        Vehiculo vehiculo = vehiculoRepository.findByPlacaAndParqueaderoIdAndFechaSalidaIsNull(
                placaNormalizada, request.getParqueaderoId())
                .orElseThrow(() -> {
                    log.warn("Intento de registrar salida de placa {} que no está en el parqueadero ID: {}",
                            placaNormalizada, request.getParqueaderoId());
                    return new BadRequestException(
                            "No se puede Registrar Salida, no existe la placa en el parqueadero");
                });

        LocalDateTime fechaSalida = LocalDateTime.now();

        VehiculoHistorial historial = VehiculoHistorial.builder()
                .placa(vehiculo.getPlaca())
                .parqueadero(vehiculo.getParqueadero())
                .fechaIngreso(vehiculo.getFechaIngreso())
                .fechaSalida(fechaSalida)
                .build();

        vehiculoHistorialRepository.save(historial);
        log.info("Registro movido al historial con ID: {}", historial.getId());

        vehiculoRepository.delete(vehiculo);

        log.info("Salida registrada exitosamente para vehículo con placa: {}", placaNormalizada);

        return SalidaResponse.builder()
                .mensaje("Salida registrada")
                .build();
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculosEnParqueadero(Long parqueaderoId) {
        log.info("Listando vehículos actualmente en el parqueadero ID: {}", parqueaderoId);

        Parqueadero parqueadero = parqueaderoRepository.findById(parqueaderoId)
                .orElseThrow(() -> new BadRequestException(
                        "El parqueadero con ID " + parqueaderoId + " no existe"));

        List<Vehiculo> vehiculos = vehiculoRepository.findAllByParqueaderoIdAndFechaSalidaIsNull(parqueaderoId);

        log.info("Se encontraron {} vehículos en el parqueadero ID: {}", vehiculos.size(), parqueaderoId);

        return vehiculos.stream()
                .map(vehiculo -> VehiculoResponse.builder()
                        .id(vehiculo.getId())
                        .placa(vehiculo.getPlaca())
                        .fechaIngreso(vehiculo.getFechaIngreso())
                        .parqueaderoId(vehiculo.getParqueadero().getId())
                        .parqueaderoNombre(vehiculo.getParqueadero().getNombre())
                        .build())
                .collect(Collectors.toList());
    }
}
