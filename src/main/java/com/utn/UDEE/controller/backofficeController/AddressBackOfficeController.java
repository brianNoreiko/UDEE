package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backoffice/address")
public class AddressBackOfficeController {

    @Autowired
    AddressService addressService;
/*
    @GetMapping
    public List<Address> getAllAddresses(){ return addressService.getAllAddresses();}

    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable Integer id){ return addressService.getAddressById(id);}

    @PostMapping
    public void addNewAddress(@RequestBody Address address){ addressService.addNewAddress(address);}
*/

}
