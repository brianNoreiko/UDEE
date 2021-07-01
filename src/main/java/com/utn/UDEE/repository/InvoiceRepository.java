package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Integer> {

    @Query(value = "select iv.* from invoices iv\n" +
            "join users u on u.user_id = iv.user_id\n" +
            "where u.user_id = :uId or u.user_id=1 and date between :since and :until",nativeQuery = true)
    Page<Invoice> findAllByUserAndDateBetween(@Param("uId") User user,
                                              @Param("since") LocalDateTime since,
                                              @Param("until") LocalDateTime until,
                                              Pageable pageable);

    Page<Invoice> findAllByUserAndPayed(User user, boolean payed, Pageable pageable);

    Page<Invoice> findAllByAddressAndPayed(Address address, boolean b, Pageable pageable);

    Page<Invoice> findAllByUser(User user, Pageable pageable);

    @Procedure(value = "p_all_clients_invoice")
    void p_invoicing_update_rate();
}
