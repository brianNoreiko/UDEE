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

    User findByEmailAndPassword(String email, String password);

    /*@Query(value =
            "SELECT consum.id_user AS id, consum.name,consum.last_name as lastname, consum.username, ( SUM(consum.max) - SUM(consum.min) ) AS consumption\n" +
                    "FROM\n" +
                    "\t\t( SELECT m.id_meter, u.id_user, u.name, u.last_name, u.username, MAX(me.quantity_kw) AS max, MIN(me.quantity_kw) AS min, me.date\n" +
                    "\t\t\t FROM users AS u\n" +
                    "\t\t\t\t\t  JOIN addresses AS a\n" +
                    "\t\t\t\t\t\t   ON u.id_user = a.id_user\n" +
                    "\t\t\t\t\t  JOIN meters AS m\n" +
                    "\t\t\t\t\t\t   ON a.id_meter = m.id_meter\n" +
                    "\t\t\t\t\t  JOIN measurements AS me\n" +
                    "\t\t\t\t\t\t   ON m.id_meter = me.id_meter\n" +
                    "\t\tGROUP BY m.id_meter, u.id_user, u.name, u.last_name, u.username\n" +
                    "        HAVING me.date BETWEEN ?1 AND ?2 ) consum\n" +
                    "GROUP BY id, consum.name, consum.last_name, consum.username ORDER BY consumption DESC LIMIT 10;", nativeQuery = true)
    List<User> getTopTenConsumers(LocalDate since, LocalDate until);*/
}
