package com.utn.UDEE.repository;

import com.utn.UDEE.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
