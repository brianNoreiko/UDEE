package com.utn.UDEE.repository;

import com.utn.UDEE.models.Client;
import com.utn.UDEE.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PersonRepository extends JpaRepository<Person, Long>{
}
