package com.utn.UDEE.repository;

import com.utn.UDEE.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameOrEmail(String username, String email);

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findByUsernameAndPassword(String username, String password);

    //List<User> getTopTenConsumers(LocalDate since, LocalDate until);*/
}
