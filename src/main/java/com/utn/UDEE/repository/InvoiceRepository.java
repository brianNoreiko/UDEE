package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Integer> {


    Page<Invoice> findAllByUserAndDateBetween(User user, LocalDateTime since, LocalDateTime until, Pageable pageable);

    Page<Invoice> findAllByUserAndPayed(User user, boolean payed, Pageable pageable);

    Page<Invoice> findAllByAddressAndPayed(Address address, boolean b, Pageable pageable);

    Page<Invoice> findAllByUser(User user, Pageable pageable);

    @Procedure(value = "p_all_clients_invoice")
    void p_invoicing_update_rate();
}
