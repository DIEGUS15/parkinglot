package com.parkingLot.services;

import com.parkingLot.dto.ParqueaderoRequest;
import com.parkingLot.dto.ParqueaderoResponse;
import com.parkingLot.entities.Parqueadero;
import com.parkingLot.entities.User;
import com.parkingLot.exceptions.BadRequestException;
import com.parkingLot.repositories.ParqueaderoRepository;
import com.parkingLot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParqueaderoService {

    private final ParqueaderoRepository parqueaderoRepository;
    private final UserRepository userRepository;

    @Transactional
    public ParqueaderoResponse crear(ParqueaderoRequest request) {
        if (parqueaderoRepository.existsByNombre(request.getNombre())) {
            throw new BadRequestException("Ya existe un parqueadero con ese nombre");
        }

        User socio = userRepository.findById(request.getSocioId())
                .orElseThrow(() -> new BadRequestException("El socio no existe"));

        if (!socio.getRole().getNombre().equals("SOCIO")) {
            throw new BadRequestException("El usuario especificado no es un socio");
        }

        if (!socio.isActive()) {
            throw new BadRequestException("El socio está inactivo");
        }

        Parqueadero parqueadero = Parqueadero.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .capacidadMaxima(request.getCapacidadMaxima())
                .costoPorHora(request.getCostoPorHora())
                .socio(socio)
                .activo(true)
                .build();

        parqueaderoRepository.save(parqueadero);

        return mapToResponse(parqueadero);
    }

    @Transactional(readOnly = true)
    public List<ParqueaderoResponse> listarTodos() {
        return parqueaderoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParqueaderoResponse> listarActivos() {
        return parqueaderoRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParqueaderoResponse obtenerPorId(Long id) {
        Parqueadero parqueadero = parqueaderoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Parqueadero no encontrado"));

        return mapToResponse(parqueadero);
    }

    @Transactional(readOnly = true)
    public List<ParqueaderoResponse> listarPorSocio(Long socioId) {
        User socio = userRepository.findById(socioId)
                .orElseThrow(() -> new BadRequestException("Socio no encontrado"));

        return parqueaderoRepository.findBySocio(socio).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParqueaderoResponse actualizar(Long id, ParqueaderoRequest request) {
        Parqueadero parqueadero = parqueaderoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Parqueadero no encontrado"));

        if (!parqueadero.getNombre().equals(request.getNombre()) &&
                parqueaderoRepository.existsByNombre(request.getNombre())) {
            throw new BadRequestException("Ya existe un parqueadero con ese nombre");
        }

        User socio = userRepository.findById(request.getSocioId())
                .orElseThrow(() -> new BadRequestException("El socio no existe"));

        if (!socio.getRole().getNombre().equals("SOCIO")) {
            throw new BadRequestException("El usuario especificado no es un socio");
        }

        if (!socio.isActive()) {
            throw new BadRequestException("El socio está inactivo");
        }

        parqueadero.setNombre(request.getNombre());
        parqueadero.setDireccion(request.getDireccion());
        parqueadero.setCapacidadMaxima(request.getCapacidadMaxima());
        parqueadero.setCostoPorHora(request.getCostoPorHora());
        parqueadero.setSocio(socio);

        parqueaderoRepository.save(parqueadero);

        return mapToResponse(parqueadero);
    }

    @Transactional
    public void eliminar(Long id) {
        Parqueadero parqueadero = parqueaderoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Parqueadero no encontrado"));

        parqueadero.setActivo(false);
        parqueaderoRepository.save(parqueadero);
    }

    @Transactional
    public void eliminarDefinitivamente(Long id) {
        if (!parqueaderoRepository.existsById(id)) {
            throw new BadRequestException("Parqueadero no encontrado");
        }

        parqueaderoRepository.deleteById(id);
    }

    @Transactional
    public ParqueaderoResponse activar(Long id) {
        Parqueadero parqueadero = parqueaderoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Parqueadero no encontrado"));

        parqueadero.setActivo(true);
        parqueaderoRepository.save(parqueadero);

        return mapToResponse(parqueadero);
    }

    private ParqueaderoResponse mapToResponse(Parqueadero parqueadero) {
        return ParqueaderoResponse.builder()
                .id(parqueadero.getId())
                .nombre(parqueadero.getNombre())
                .direccion(parqueadero.getDireccion())
                .capacidadMaxima(parqueadero.getCapacidadMaxima())
                .costoPorHora(parqueadero.getCostoPorHora())
                .socio(ParqueaderoResponse.SocioInfo.builder()
                        .id(parqueadero.getSocio().getId())
                        .nombre(parqueadero.getSocio().getNombre())
                        .email(parqueadero.getSocio().getEmail())
                        .build())
                .activo(parqueadero.getActivo())
                .createdAt(parqueadero.getCreatedAt())
                .updatedAt(parqueadero.getUpdatedAt())
                .build();
    }
}
