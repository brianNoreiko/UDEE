package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement,Integer> {
    @Query(value = "select * from measurements where meterId = mId and date between since and until",nativeQuery = true)
    List<Measurement> findAllByMeterAndDateBetween(@Param("mId")Meter meter,@Param("since") LocalDateTime since, @Param("until") LocalDateTime until);

    @Query(value = "select * from measurements where meterId = mId and date between since and until", nativeQuery = true)
    Page<Measurement> getAllByMeterAndDateBetween(
            @Param("mId") Meter meter,
            @Param("since") LocalDateTime since,
            @Param("until") LocalDateTime until,
            Pageable pageable);

    Page<Measurement> getMeasurementByAddressBetweenDate(Address address, LocalDateTime since, LocalDateTime until, Pageable pageable);

    @Query(value = "select ms.* from users u join addresses a ON a.userId=u.userId \n" +
            "        JOIN meters mt ON mt.serial_number= a.meterId \n" +
            "        JOIN measurements ms ON mt.serial_number=ms.meterId")
    Page<Measurement> findAllMeasurementsByUser(@Param("UserID") User user, Pageable pageable);

    @Procedure(procedureName = "p_consult_User_measurements_byDates")
    Page<ResultSet> getMeasurementByUserBetweenDate(String username, LocalDateTime since, LocalDateTime until, Pageable pageable);

}
