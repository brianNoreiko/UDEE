package com.utn.UDEE.repository;

import com.utn.UDEE.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface invoiceRepository extends JpaRepository<Invoice,Long> {
}
