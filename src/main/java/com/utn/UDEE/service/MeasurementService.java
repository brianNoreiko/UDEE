package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.repository.MeasurementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class MeasurementService {

    MeasurementRepository measurementRepository;
    MeterService meterService;


    public Optional<ClientConsuption> getConsumptionByMeterAndBetweenDate(Integer idMeter, LocalDate since, LocalDate until) throws ResourceException {
        Meter meter = meterService.getMeterById(idMeter);
        User user = meter.getAddress().getUser();

        Double ConsumptionKw = 0.0;
        Double consumptionCost = 0.0;
        Integer measurementsCount = 0;

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
}

