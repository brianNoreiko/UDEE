package com.utn.UDEE.repository;

import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> getAllUsers();

    User findByUsernameOrEmail(String username, String email);

    User findByEmail(String email);
}
