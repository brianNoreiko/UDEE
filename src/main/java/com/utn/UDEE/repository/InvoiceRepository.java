package com.utn.UDEE.repository;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Integer> {
    Page<Invoice> findAllByUserAndDateBetween(User user, LocalDate since, LocalDate until, Pageable pageable);

    Page<Invoice> findAllByUserAndPayed(User user, boolean payed, Pageable pageable);

    Page<Invoice> findAllByAddressAndPayed(Address address, boolean b, Pageable pageable);
}
