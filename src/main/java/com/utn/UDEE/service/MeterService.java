package com.utn.UDEE.service;

import com.utn.UDEE.models.Meter;
import com.utn.UDEE.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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



    public void addMeter(Meter meter) { meterRepository.save(meter); }
}
