package com.utn.UDEE.repository;

import com.utn.UDEE.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface clientRepository extends JpaRepository<Client, Long>{
}
