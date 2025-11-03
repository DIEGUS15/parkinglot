package com.parkingLot.repositories;

import com.parkingLot.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    @Query("SELECT v FROM Vehiculo v WHERE v.placa = :placa AND v.fechaSalida IS NULL AND v.activo = true")
    Optional<Vehiculo> findByPlacaAndFechaSalidaIsNull(@Param("placa") String placa);

    boolean existsByPlacaAndFechaSalidaIsNullAndActivoTrue(String placa);

    @Query("SELECT v FROM Vehiculo v WHERE v.placa = :placa AND v.parqueadero.id = :parqueaderoId AND v.fechaSalida IS NULL AND v.activo = true")
    Optional<Vehiculo> findByPlacaAndParqueaderoIdAndFechaSalidaIsNull(@Param("placa") String placa,
            @Param("parqueaderoId") Long parqueaderoId);

    @Query("SELECT v FROM Vehiculo v WHERE v.parqueadero.id = :parqueaderoId AND v.fechaSalida IS NULL AND v.activo = true ORDER BY v.fechaIngreso DESC")
    List<Vehiculo> findAllByParqueaderoIdAndFechaSalidaIsNull(@Param("parqueaderoId") Long parqueaderoId);

    @Query("SELECT COUNT(v) FROM Vehiculo v WHERE v.parqueadero.id = :parqueaderoId AND v.fechaSalida IS NULL AND v.activo = true")
    long countByParqueaderoIdAndFechaSalidaIsNull(@Param("parqueaderoId") Long parqueaderoId);

    @Query("SELECT v FROM Vehiculo v WHERE UPPER(v.placa) LIKE UPPER(CONCAT('%', :searchTerm, '%')) AND v.fechaSalida IS NULL AND v.activo = true ORDER BY v.fechaIngreso DESC")
    List<Vehiculo> findByPlacaContainingIgnoreCaseAndFechaSalidaIsNull(@Param("searchTerm") String searchTerm);
}
