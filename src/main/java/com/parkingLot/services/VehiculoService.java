package com.parkingLot.services;

import com.parkingLot.dto.GananciasResponse;
import com.parkingLot.dto.IngresoRequest;
import com.parkingLot.dto.IngresoResponse;
import com.parkingLot.dto.SalidaRequest;
import com.parkingLot.dto.SalidaResponse;
import com.parkingLot.dto.TopVehiculoResponse;
import com.parkingLot.dto.VehiculoPrimeraVezResponse;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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

    @Transactional(readOnly = true)
    public List<TopVehiculoResponse> obtenerTop10VehiculosMasFrecuentes() {
        log.info("Obteniendo los 10 vehículos más frecuentes");

        List<Object[]> resultados = vehiculoHistorialRepository.findTop10VehiculosMasFrecuentes();

        List<TopVehiculoResponse> topVehiculos = resultados.stream()
                .map(result -> TopVehiculoResponse.builder()
                        .placa((String) result[0])
                        .cantidadRegistros((Long) result[1])
                        .build())
                .collect(Collectors.toList());

        log.info("Se encontraron {} vehículos en el ranking", topVehiculos.size());

        return topVehiculos;
    }

    @Transactional(readOnly = true)
    public List<TopVehiculoResponse> obtenerTop10VehiculosMasFrecuentesPorParqueadero(Long parqueaderoId) {
        log.info("Obteniendo los 10 vehículos más frecuentes del parqueadero ID: {}", parqueaderoId);

        // Validar que el parqueadero exista
        parqueaderoRepository.findById(parqueaderoId)
                .orElseThrow(() -> new BadRequestException(
                        "El parqueadero con ID " + parqueaderoId + " no existe"));

        List<Object[]> resultados = vehiculoHistorialRepository.findTop10VehiculosMasFrecuentesByParqueadero(parqueaderoId);

        List<TopVehiculoResponse> topVehiculos = resultados.stream()
                .map(result -> TopVehiculoResponse.builder()
                        .placa((String) result[0])
                        .cantidadRegistros((Long) result[1])
                        .build())
                .collect(Collectors.toList());

        log.info("Se encontraron {} vehículos en el ranking del parqueadero ID: {}", topVehiculos.size(), parqueaderoId);

        return topVehiculos;
    }

    @Transactional(readOnly = true)
    public List<VehiculoPrimeraVezResponse> obtenerVehiculosPrimeraVezEnParqueadero(Long parqueaderoId) {
        log.info("Obteniendo vehículos que están por primera vez en el parqueadero ID: {}", parqueaderoId);

        // Validar que el parqueadero exista
        parqueaderoRepository.findById(parqueaderoId)
                .orElseThrow(() -> new BadRequestException(
                        "El parqueadero con ID " + parqueaderoId + " no existe"));

        // Obtener todos los vehículos actualmente en el parqueadero
        List<Vehiculo> vehiculosActuales = vehiculoRepository.findAllByParqueaderoIdAndFechaSalidaIsNull(parqueaderoId);

        // Filtrar solo los que son primera vez (no tienen historial en este parqueadero)
        List<VehiculoPrimeraVezResponse> vehiculosPrimeraVez = vehiculosActuales.stream()
                .map(vehiculo -> {
                    boolean esPrimeraVez = !vehiculoHistorialRepository.existsByPlacaAndParqueaderoId(
                            vehiculo.getPlaca(), parqueaderoId);

                    return VehiculoPrimeraVezResponse.builder()
                            .id(vehiculo.getId())
                            .placa(vehiculo.getPlaca())
                            .fechaIngreso(vehiculo.getFechaIngreso())
                            .esPrimeraVez(esPrimeraVez)
                            .build();
                })
                .filter(VehiculoPrimeraVezResponse::getEsPrimeraVez)
                .collect(Collectors.toList());

        log.info("Se encontraron {} vehículos por primera vez en el parqueadero ID: {}",
                vehiculosPrimeraVez.size(), parqueaderoId);

        return vehiculosPrimeraVez;
    }

    @Transactional(readOnly = true)
    public GananciasResponse obtenerGananciasHoy(Long parqueaderoId) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        return calcularGanancias(parqueaderoId, inicioHoy, finHoy, "Hoy");
    }

    @Transactional(readOnly = true)
    public GananciasResponse obtenerGananciasSemana(Long parqueaderoId) {
        LocalDateTime inicioSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime finSemana = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
        return calcularGanancias(parqueaderoId, inicioSemana, finSemana, "Esta semana");
    }

    @Transactional(readOnly = true)
    public GananciasResponse obtenerGananciasMes(Long parqueaderoId) {
        LocalDateTime inicioMes = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        return calcularGanancias(parqueaderoId, inicioMes, finMes, "Este mes");
    }

    @Transactional(readOnly = true)
    public GananciasResponse obtenerGananciasAnio(Long parqueaderoId) {
        LocalDateTime inicioAnio = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime finAnio = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).atTime(LocalTime.MAX);
        return calcularGanancias(parqueaderoId, inicioAnio, finAnio, "Este año");
    }

    private GananciasResponse calcularGanancias(Long parqueaderoId, LocalDateTime fechaInicio, LocalDateTime fechaFin, String periodo) {
        log.info("Calculando ganancias del parqueadero ID: {} para el periodo: {}", parqueaderoId, periodo);

        // Validar que el parqueadero exista
        Parqueadero parqueadero = parqueaderoRepository.findById(parqueaderoId)
                .orElseThrow(() -> new BadRequestException(
                        "El parqueadero con ID " + parqueaderoId + " no existe"));

        // Obtener registros del historial en el rango de fechas
        List<VehiculoHistorial> registros = vehiculoHistorialRepository.findByParqueaderoIdAndFechaSalidaBetween(
                parqueaderoId, fechaInicio, fechaFin);

        // Calcular ganancias totales
        BigDecimal totalGanancias = registros.stream()
                .map(registro -> calcularCostoEstadia(registro, parqueadero.getCostoPorHora()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Ganancias calculadas para {} en parqueadero ID {}: {} ({} vehículos)",
                periodo, parqueaderoId, totalGanancias, registros.size());

        return GananciasResponse.builder()
                .periodo(periodo)
                .totalGanancias(totalGanancias)
                .cantidadVehiculos((long) registros.size())
                .parqueaderoId(parqueadero.getId())
                .parqueaderoNombre(parqueadero.getNombre())
                .build();
    }

    private BigDecimal calcularCostoEstadia(VehiculoHistorial registro, BigDecimal costoPorHora) {
        Duration duracion = Duration.between(registro.getFechaIngreso(), registro.getFechaSalida());
        long minutos = duracion.toMinutes();

        // Convertir minutos a horas (redondear hacia arriba)
        BigDecimal horas = BigDecimal.valueOf(minutos)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.UP);

        // Calcular costo total
        return costoPorHora.multiply(horas).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> buscarVehiculosPorPlaca(String searchTerm) {
        log.info("Buscando vehículos con placa que contenga: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("El término de búsqueda no puede estar vacío");
        }

        String searchTermNormalizado = searchTerm.trim().toUpperCase();

        List<Vehiculo> vehiculos = vehiculoRepository
                .findByPlacaContainingIgnoreCaseAndFechaSalidaIsNull(searchTermNormalizado);

        log.info("Se encontraron {} vehículos con placas que contienen '{}'", vehiculos.size(), searchTermNormalizado);

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
