package com.parkingLot.repositories;

import com.parkingLot.entities.Parqueadero;
import com.parkingLot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParqueaderoRepository extends JpaRepository<Parqueadero, Long> {

    List<Parqueadero> findByActivoTrue();

    List<Parqueadero> findBySocio(User socio);

    List<Parqueadero> findBySocioAndActivoTrue(User socio);

    Optional<Parqueadero> findByIdAndActivoTrue(Long id);

    boolean existsByNombre(String nombre);
}
