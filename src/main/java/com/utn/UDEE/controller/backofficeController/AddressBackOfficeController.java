package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.alreadyExist.AddressAlreadyExist;
import com.utn.UDEE.exception.doesNotExist.AddressDoesNotExist;
import com.utn.UDEE.exception.doesNotExist.MeterDoesNotExist;
import com.utn.UDEE.exception.doesNotExist.RateDoesNotExist;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.AddressService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backoffice/address")
public class AddressBackOfficeController {

    @Autowired
    AddressService addressService;

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addNewAddress(@RequestBody Address address) throws AddressAlreadyExist {
        Address addressAdded = addressService.addNewAddress(address);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .location(EntityURLBuilder.buildURL("addresses",addressAdded.getId()))
                .body(EntityResponse.messageResponse("Address created successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateAddress(@PathVariable Integer idToUp,
                                                  @RequestBody Address address) throws PrimaryKeyViolationException, AddressAlreadyExist {
        addressService.updateAddress(idToUp,address);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("Address updated successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}/meters/{idMeter}")
    public ResponseEntity<Response> addMeterToAddress(@PathVariable Integer id, @PathVariable Integer idMeter) throws AddressDoesNotExist, MeterDoesNotExist {
        addressService.addMeterToAddress(id, idMeter);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(EntityResponse.messageResponse("The address has been modified"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}/rates/{idRate}")
    public ResponseEntity<Response> addRateToAddress(@PathVariable Integer id, @PathVariable Integer idRate) throws AddressDoesNotExist, RateDoesNotExist {
        addressService.addRateToAddress(id, idRate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(EntityResponse.messageResponse("The address has been modified"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAddressById(@PathVariable Integer id) throws AddressDoesNotExist, DeleteException {
        addressService.deleteAddressById(id);
        return ResponseEntity.accepted().build();
    }

}
