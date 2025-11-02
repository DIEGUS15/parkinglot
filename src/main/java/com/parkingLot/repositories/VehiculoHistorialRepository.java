package com.parkingLot.repositories;

import com.parkingLot.entities.VehiculoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoHistorialRepository extends JpaRepository<VehiculoHistorial, Long> {
}
