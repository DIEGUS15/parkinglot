package com.parkingLot.repositories;

import com.parkingLot.entities.VehiculoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehiculoHistorialRepository extends JpaRepository<VehiculoHistorial, Long> {

       @Query("SELECT vh.placa as placa, COUNT(vh) as cantidadRegistros " +
                     "FROM VehiculoHistorial vh " +
                     "GROUP BY vh.placa " +
                     "ORDER BY COUNT(vh) DESC " +
                     "LIMIT 10")
       List<Object[]> findTop10VehiculosMasFrecuentes();

       @Query("SELECT vh.placa as placa, COUNT(vh) as cantidadRegistros " +
                     "FROM VehiculoHistorial vh " +
                     "WHERE vh.parqueadero.id = :parqueaderoId " +
                     "GROUP BY vh.placa " +
                     "ORDER BY COUNT(vh) DESC " +
                     "LIMIT 10")
       List<Object[]> findTop10VehiculosMasFrecuentesByParqueadero(@Param("parqueaderoId") Long parqueaderoId);

       @Query("SELECT COUNT(vh) > 0 FROM VehiculoHistorial vh " +
                     "WHERE vh.placa = :placa AND vh.parqueadero.id = :parqueaderoId")
       boolean existsByPlacaAndParqueaderoId(@Param("placa") String placa, @Param("parqueaderoId") Long parqueaderoId);

       @Query("SELECT vh FROM VehiculoHistorial vh " +
                     "WHERE vh.parqueadero.id = :parqueaderoId " +
                     "AND vh.fechaSalida >= :fechaInicio " +
                     "AND vh.fechaSalida <= :fechaFin")
       List<VehiculoHistorial> findByParqueaderoIdAndFechaSalidaBetween(
                     @Param("parqueaderoId") Long parqueaderoId,
                     @Param("fechaInicio") LocalDateTime fechaInicio,
                     @Param("fechaFin") LocalDateTime fechaFin);
}
