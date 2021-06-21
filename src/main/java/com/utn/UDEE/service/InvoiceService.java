package com.utn.UDEE.service;

import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import com.utn.UDEE.repository.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InvoiceService {

    InvoiceRepository invoiceRepository;
    UserService userService;


    public Page<Invoice> getInvoiceBetweenDateByUser(Integer idUser, LocalDate since, LocalDate until, Pageable pageable) {
        User user = userService.getUserById(idUser);
        return invoiceRepository.findAllByUserAndDateBetween(user, since, until, pageable);
    }

    public Page<Invoice> getUnpaidByUser(Integer idUser, Pageable pageable) {
        User user = userService.getUserById(idUser);
        return invoiceRepository.findAllByUserAndPayed(user, false, pageable);
    }
}
