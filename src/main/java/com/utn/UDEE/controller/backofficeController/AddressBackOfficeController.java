package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.dto.AddressDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.AddressService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/backoffice/address")
public class AddressBackOfficeController {


    AddressService addressService;
    ConversionService conversionService;

    @Autowired
    public AddressBackOfficeController(AddressService addressService, ConversionService conversionService) {
        this.addressService = addressService;
        this.conversionService = conversionService;
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<AddressDto>> getAllAddresses(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Address> addressPage = addressService.getAllAddresses(pageable);
        Page<AddressDto> addressDtoPage = addressPage.map(address -> conversionService.convert(addressPage, AddressDto.class));
        return EntityResponse.listResponse(addressDtoPage);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Integer id) throws HttpClientErrorException {
        AddressDto addressDto = conversionService.convert(addressService.getAddressById(id), AddressDto.class);
        return ResponseEntity.ok(addressDto);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addNewAddress(@RequestBody Address address) throws ResourceAlreadyExistException{
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
                                                  @RequestBody Address address) throws PrimaryKeyViolationException, ResourceAlreadyExistException {
        addressService.updateAddress(idToUp,address);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("Address updated successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}/meters/{idMeter}")
    public ResponseEntity<Response> addMeterToAddress(@PathVariable Integer id, @PathVariable Integer idMeter) throws ResourceAlreadyExistException, ResourceDoesNotExistException {
        addressService.addMeterToAddress(id, idMeter);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(EntityResponse.messageResponse("The meter has been added"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}/rates/{idRate}")
    public ResponseEntity<Response> addRateToAddress(@PathVariable Integer id, @PathVariable Integer idRate) throws ResourceAlreadyExistException, ResourceDoesNotExistException {
        addressService.addRateToAddress(id, idRate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(EntityResponse.messageResponse("The rate has been added"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAddressById(@PathVariable Integer id) throws ResourceDoesNotExistException, DeleteException {
        addressService.deleteAddressById(id);
        return ResponseEntity.accepted().build();
    }

}
