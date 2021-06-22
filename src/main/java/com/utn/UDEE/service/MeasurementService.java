package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.repository.MeasurementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Optional;

@Service
public class MeasurementService {

    MeasurementRepository measurementRepository;
    MeterService meterService;
    AddressService addressService;


    public Optional<ClientConsuption> getConsumptionByMeterAndBetweenDate(Integer idMeter, LocalDate since, LocalDate until) throws ResourceException {
        Meter meter = meterService.getMeterById(idMeter);
        Optional<ClientConsuption> clientConsuption = Optional.of(new ClientConsuption());

        LinkedList<Measurement> measurementList = (LinkedList<Measurement>) measurementRepository.findAllByMeterAndDateBetween(meter, since, until);

        if(!measurementList.isEmpty()){
            measurementList.forEach(o -> clientConsuption.get()
                    .setConsumptionCost(clientConsuption.get().getConsumptionCost() + o.getKwhPrice()));

            clientConsuption.get().setConsumptionCost(measurementList.getLast().getKwhPrice() - measurementList.getFirst().getKwhPrice());

        } else{
            throw new ResourceException("Inexistent resource");
        }


        return clientConsuption;
    }

    public Page<Measurement> getAllByMeterAndBetweenDate(Integer idMeter, LocalDate since, LocalDate until, Pageable pageable) {
        Meter meter = meterService.getMeterById(idMeter);

        return measurementRepository.getAllByMeterAndBetweenDate(meter,since,until, pageable);
    }

    public Page<Measurement> getMeasurementByAddressBetweenDate(Integer idAddress, LocalDate since, LocalDate until, Pageable pageable) {
        Address address = addressService.getAddressById(idAddress);

        return measurementRepository.getMeasurementByAddressBetweenDate(address,since,until,pageable);
    }

    public Page<Measurement> getAllMeasurements(Pageable pageable) {
        return measurementRepository.findAll(pageable);
    }

    /*public Page<Measurement> getTopTenConsumers(Integer size, Sort.Order topten) {
        Pageable pageable = PageRequest.of(0,size);
        return measurementRepository.getTopTenConsumers(topten,pageable);
    }*/
}

