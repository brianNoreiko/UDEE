package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.repository.MeasurementRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
public class MeasurementService {

    MeasurementRepository measurementRepository;
    MeterService meterService;
    AddressService addressService;
    UserService userService;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, MeterService meterService, AddressService addressService, UserService userService) {
        this.measurementRepository = measurementRepository;
        this.meterService = meterService;
        this.addressService = addressService;
        this.userService = userService;
    }

    public Optional<ClientConsuption> getConsumptionByMeterAndBetweenDate(Integer idMeter, LocalDateTime since, LocalDateTime until) throws ResourceDoesNotExistException {
        Meter meter = meterService.getMeterById(idMeter);
        Optional<ClientConsuption> clientConsuption = Optional.of(new ClientConsuption());

        LinkedList<Measurement> measurementList = (LinkedList<Measurement>) measurementRepository.findAllByMeterAndDateBetween(meter, since, until);

        if(!measurementList.isEmpty()){
            measurementList.forEach(o -> clientConsuption.get()
                    .setConsumptionCost(clientConsuption.get().getConsumptionCost() + o.getKwhPrice()));

            clientConsuption.get().setConsumptionCost(measurementList.getLast().getKwhPrice() - measurementList.getFirst().getKwhPrice());

        } else{
            throw new ResourceDoesNotExistException("Inexistent resource");
        }


        return clientConsuption;
    }

    @SneakyThrows
    public Page<Measurement> getAllByMeterAndBetweenDate(Integer idMeter, LocalDateTime since, LocalDateTime until, Pageable pageable) {
        Meter meter = meterService.getMeterById(idMeter);

        return measurementRepository.getAllByMeterAndBetweenDate(meter,since,until, pageable);
    }

    public Page<Measurement> getMeasurementByAddressBetweenDate(Integer idAddress, LocalDateTime since, LocalDateTime until, Pageable pageable) throws ResourceDoesNotExistException {
        Address address = addressService.getAddressById(idAddress);

        return measurementRepository.getMeasurementByAddressBetweenDate(address,since,until,pageable);
    }

    public Page<Measurement> getAllMeasurements(Pageable pageable) {
        return measurementRepository.findAll(pageable);
    }

    public Measurement addMeasurement(Measurement measurement) throws ResourceAlreadyExistException, ResourceDoesNotExistException {
        Measurement measurementExist = getMeasurementById(measurement.getId());
        if(isNull(measurementExist)){
            return measurementRepository.save(measurement);
        }else{
            throw new ResourceAlreadyExistException("Measurement already exists");
        }
    }

    public Measurement getMeasurementById(Integer id) throws ResourceDoesNotExistException {
        return measurementRepository.findById(id).orElseThrow(()->new ResourceDoesNotExistException("Measurement doesn't exist"));
    }


    public Page<Measurement> getAllMeasurementsByUser(Integer idUser, Pageable pageable) throws ResourceDoesNotExistException {
        User user = userService.getUserById(idUser);

        return measurementRepository.findAllMeasurementsByUser(user, pageable);
    }
}

