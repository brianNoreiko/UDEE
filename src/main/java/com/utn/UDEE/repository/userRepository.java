package com.utn.UDEE.repository;

import com.utn.UDEE.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface userRepository extends JpaRepository<User,Long> {
}
