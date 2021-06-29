package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement,Integer> {
    List<Measurement> findAllByMeterAndDateBetween(Meter meter, LocalDateTime since, LocalDateTime until);

    Page<Measurement> getAllByMeterAndBetweenDate(Meter meter, LocalDateTime since, LocalDateTime until, Pageable pageable);

    Page<Measurement> getMeasurementByAddressBetweenDate(Address address, LocalDateTime since, LocalDateTime until, Pageable pageable);

    Page<Measurement> findAllMeasurementsByUser(User user, Pageable pageable);

    @Procedure(procedureName = "p_consult_User_measurements_byDates")
    Page<ResultSet> getMeasurementByUserBetweenDate(String username, LocalDateTime since, LocalDateTime until, Pageable pageable);

}
