package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import com.utn.UDEE.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Service
public class InvoiceService {

    InvoiceRepository invoiceRepository;
    UserService userService;
    AddressService addressService;
    MeasurementService measurementService;
    MeterService meterService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, UserService userService, AddressService addressService, MeasurementService measurementService, MeterService meterService) {
        this.invoiceRepository = invoiceRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.measurementService = measurementService;
        this.meterService = meterService;
    }

    public Page<Invoice> getInvoiceBetweenDateByUser(Integer idUser, LocalDateTime since, LocalDateTime until, Pageable pageable) throws ResourceDoesNotExistException {
        User user = userService.getUserById(idUser);
        if (user == null) {
            throw new ResourceDoesNotExistException("User doesn't exist");
        } else {
            return invoiceRepository.findAllByUserAndDateBetween(user, since, until, pageable);
        }
    }
    public Page<Invoice> getUnpaidByUser(Integer idUser, Pageable pageable) {
        User user = userService.getUserById(idUser);
        return invoiceRepository.findAllByUserAndPayed(user, false, pageable);
    }

    public Invoice addNewInvoice(Invoice invoice) throws ResourceAlreadyExistException {

        Invoice invoiceExist = getInvoiceById(invoice.getId());
        if(isNull(invoiceExist)){
            return invoiceRepository.save(invoice);
        }else{
            throw new ResourceAlreadyExistException("Invoice already exists");
        }
    }


    public void deleteInvoiceById(Integer idInvoice) throws ResourceDoesNotExistException, DeleteException {
        Invoice invoice = getInvoiceById(idInvoice);
        if(invoice == null){
            throw new ResourceDoesNotExistException("Invoice doesn't exist");
        }
        if(isNull(invoice.getUser())) {
            invoiceRepository.deleteById(idInvoice);
        } else {
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }

    public Invoice getInvoiceById(Integer idInvoice) {
        return invoiceRepository.findById(idInvoice).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }


    public Page<Invoice> getAllUnpaidByAddress(Integer idAddress, Pageable pageable) throws ResourceDoesNotExistException {
        Address address = addressService.getAddressById(idAddress);
        return invoiceRepository.findAllByAddressAndPayed(address,false,pageable);
    }

    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> getAllInvoicesByUser(Integer idUser, Pageable pageable) {
        User user = userService.getUserById(idUser);

        return invoiceRepository.findAllByUser(user, pageable);
    }
}
