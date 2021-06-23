package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;


@Service
public class AddressService {

    AddressRepository addressRepository;
    MeterService meterService;
    RateService rateService;

    @Autowired
    public AddressService(AddressRepository addressRepository, MeterService meterService, RateService rateService) {
        this.addressRepository = addressRepository;
        this.meterService = meterService;
        this.rateService = rateService;
    }

    public Page<Address> getAllAddresses(Pageable pageable){
        return addressRepository.findAll(pageable);
    }

    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Address addNewAddress(Address address) throws ResourceAlreadyExistException{
        Address addressExists = getAddressById(address.getId());
        if(isNull(addressExists)){
            return addressRepository.save(address);
        }else{
            throw new ResourceAlreadyExistException("Address already exists");
        }
    }


    public Address updateAddress(Integer idToUp, Address address) throws ResourceAlreadyExistException, PrimaryKeyViolationException {
        Address addressToUpdate = getAddressById(idToUp);
        if(addressToUpdate.equals(address)){
            throw new ResourceAlreadyExistException("Nothing to update. The address already exist");
        }
        if(address.getId() != addressToUpdate.getId()){
            throw new PrimaryKeyViolationException("Primary key (id) cannot be changed");
        }
        return addressRepository.save(address);
    }

    public Address addMeterToAddress(Integer id, Integer idMeter) throws ResourceDoesNotExistException {
        Address address = getAddressById(id);
        Meter meter = meterService.getMeterById(idMeter);
        address.setMeter(meter);
        return addressRepository.save(address);
    }

    public Address addRateToAddress(Integer id, Integer idRate) {
        Address address = getAddressById(id);
        Rate rate = rateService.getRateById(idRate);
        address.setRate(rate);
        return  addressRepository.save(address);
    }

    public void deleteAddressById(Integer id) throws DeleteException, ResourceDoesNotExistException {
        Address address = getAddressById(id);
        if(address == null){
            throw new ResourceDoesNotExistException("Address doesn't exist");
        }
        if(isNull(address.getMeter())) {
            addressRepository.deleteById(id);
        } else {
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }
}
