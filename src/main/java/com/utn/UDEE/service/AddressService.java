package com.utn.UDEE.service;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    public List<Address> getAllAddresses(){ return addressRepository.findAll(); }

    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    //Hacer funcion de buscar por nombre o "starWith"

    public void addNewAddress(Address address) { addressRepository.save(address);} //Tendría que comprobar que el nombre de calle no esté repetido
}
