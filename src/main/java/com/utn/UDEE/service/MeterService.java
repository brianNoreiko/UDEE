package com.utn.UDEE.service;

import com.utn.UDEE.models.Client;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.Person;
import com.utn.UDEE.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MeterService {

    MeterRepository meterRepository;
    PersonService personService;

    @Autowired
    public MeterService(MeterRepository meterRepository, PersonService personService) {
        this.meterRepository = meterRepository;
        this.personService = personService;
    }

    public List<Meter> getAllMeters() { return meterRepository.findAll(); }

    public Meter getMeterById(Long id) {
        return meterRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Meter getMeterByClientId(Long idClient) {
        Meter meter = null;
        Person client = personService.getPersonById(idClient);
        if(!(client instanceof Client)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }else{
            meter = ((Client) client).getMeter();
        }
       return meter;
    }
}
