package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement,Integer> {
    @Query(value = "select * from measurements where meterId = mId and date between since and until",nativeQuery = true)
    Page<Measurement> findAllByMeterAndDateBetween(@Param("mId") Meter meter, @Param("since") LocalDateTime since, @Param("until") LocalDateTime until, Pageable pageable);

    @Query(value = "select * from measurements where meterId = mId and date between since and until",nativeQuery = true)
    List<Measurement> findAllByMeterAndDateBetween(Meter meter, LocalDateTime since,LocalDateTime until);

    @Query(value = "select * from measurements where meter_id = :mId and date between :since and :until", nativeQuery = true)
    Page<Measurement> getAllByMeterAndDateBetween(
            @Param("mId") Meter meter,
            @Param("since") LocalDateTime since,
            @Param("until") LocalDateTime until,
            Pageable pageable);

    @Query(value = "select ms.* from measurements ms\n" +
            "join meters mt on ms.meter_id=mt.serial_number\n" +
            "join addresses a on mt.serial_number =a.meter_id where a.address_id = :aId and date between :since and :until",nativeQuery = true)
    Page<Measurement> getMeasurementByAddressBetweenDate(
            @Param("aId")Address address,
            @Param("since")LocalDateTime since,
            @Param("until")LocalDateTime until, Pageable pageable);

    @Query(value = "select ms.* from measurements ms join meters mt on ms.meter_id = mt.serial_number join addresses a on mt.serial_number = a.address_id join users u on a.user_id = u.user_id where a.user_id = :UserID",nativeQuery = true)
    Page<Measurement> findAllMeasurementsByUser(@Param("UserID") User user, Pageable pageable);

    @Procedure(procedureName = "p_consult_User_measurements_byDates")
    Page<ResultSet> getMeasurementByUserBetweenDate(String username, LocalDateTime since, LocalDateTime until, Pageable pageable);

}
