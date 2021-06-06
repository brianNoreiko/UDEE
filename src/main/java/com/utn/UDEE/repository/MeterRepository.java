package com.utn.UDEE.repository;

import com.utn.UDEE.models.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends JpaRepository<Meter,Integer> {
}
