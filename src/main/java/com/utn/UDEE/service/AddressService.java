package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.doesNotExist.AddressDoesNotExist;
import com.utn.UDEE.exception.doesNotExist.MeterDoesNotExist;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.AddressRepository;
import com.utn.UDEE.exception.alreadyExist.AddressAlreadyExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;
    MeterService meterService;
    RateService rateService;

    //public Page<Address> getAllAddresses(){ return addressRepository.findAll(); }

    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Address addNewAddress(Address address) throws AddressAlreadyExist{
        Address addressExists = getAddressById(address.getId());
        if(isNull(addressExists)){
            return addressRepository.save(address);
        }else{
            throw new AddressAlreadyExist("Address already exists");
        }
    }


    public Address updateAddress(Integer idToUp, Address address) throws AddressAlreadyExist, PrimaryKeyViolationException {
        Address addressToUpdate = getAddressById(idToUp);
        if(addressToUpdate.equals(address)){
            throw new AddressAlreadyExist("Nothing to update. The address already exist");
        }
        if(address.getId() != addressToUpdate.getId()){
            throw new PrimaryKeyViolationException("Primary key (id) cannot be changed");
        }
        return addressRepository.save(address);
    }

    public Address addMeterToAddress(Integer id, Integer idMeter) throws MeterDoesNotExist, AddressDoesNotExist {
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

    public void deleteAddressById(Integer id) throws DeleteException {
        Address address = getAddressById(id);
        if(isNull(address.getMeter())) {
            addressRepository.deleteById(id);
        } else {
            throw new DeleteException("Can not delete this address because it depends of another objects");
        }
    }
}
