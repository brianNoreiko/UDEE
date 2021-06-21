package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement,Integer> {
    List<Measurement> findAllByMeterAndDateBetween(Meter meter, LocalDate since, LocalDate until);

    Page<Measurement> getAllByMeterAndBetweenDate(Meter meter, LocalDate since, LocalDate until, Pageable pageable);

    Page<Measurement> getMeasurementByAddressBetweenDate(Address address, LocalDate since, LocalDate until, Pageable pageable);
}
