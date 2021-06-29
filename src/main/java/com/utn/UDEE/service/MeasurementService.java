package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.repository.MeasurementRepository;
import com.utn.UDEE.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

@Service
public class MeasurementService {

    MeasurementRepository measurementRepository;
    MeterRepository meterRepository;
    MeterService meterService;
    AddressService addressService;
    UserService userService;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository,MeterRepository meterRepository, MeterService meterService, AddressService addressService, UserService userService) {
        this.measurementRepository = measurementRepository;
        this.meterRepository = meterRepository;
        this.meterService = meterService;
        this.addressService = addressService;
        this.userService = userService;
    }

    public Optional<ClientConsuption> getConsumptionByMeterAndBetweenDate(Integer idMeter, LocalDateTime since, LocalDateTime until) throws ResourceDoesNotExistException {
        Meter meter = meterService.getMeterById(idMeter);
        Optional<ClientConsuption> clientConsuption = Optional.of(new ClientConsuption());

        LinkedList<Measurement> measurementList = (LinkedList<Measurement>) measurementRepository.findAllByMeterAndDateBetween(meter, since, until);

        if(!measurementList.isEmpty()){
            measurementList.forEach(m -> clientConsuption.get()
                    .setConsumptionCost(clientConsuption.get().getConsumptionCost() + m.getKwhPrice()));

            clientConsuption.get().setConsumptionCost(measurementList.getLast().getKwhPrice() - measurementList.getFirst().getKwhPrice());

        } else{
            throw new ResourceDoesNotExistException("Doesn't exist any measurement!");
        }


        return clientConsuption;
    }


    public Page<Measurement> getAllByMeterAndBetweenDate(Integer idMeter, LocalDateTime since, LocalDateTime until, Pageable pageable) throws ResourceDoesNotExistException {

        Meter meter = meterService.getMeterById(idMeter);
        if(meter != null) {
            return measurementRepository.getAllByMeterAndDateBetween(meter, since, until, pageable);
        }else {
            throw new ResourceDoesNotExistException("Meter doesn't exist");
        }
    }

    public Page<Measurement> getMeasurementByAddressBetweenDate(Integer idAddress, LocalDateTime since, LocalDateTime until, Pageable pageable) throws ResourceDoesNotExistException {
        Address address = addressService.getAddressById(idAddress);
        if(address != null) {
            return measurementRepository.getMeasurementByAddressBetweenDate(address, since, until, pageable);
        }else {
            throw new ResourceDoesNotExistException("Address doesn't exist");
        }
    }

    public Page<Measurement> getAllMeasurements(Pageable pageable) {
        return measurementRepository.findAll(pageable);
    }

    public Measurement addMeasurement(DeliveredMeasureDto deliveredMeasureDto) throws ResourceDoesNotExistException {
        Boolean meterExist = meterRepository.existsById(deliveredMeasureDto.getSerialNumber());
        if(meterExist == true) {
            Meter meter = meterService.getMeterById(deliveredMeasureDto.getSerialNumber());
            Measurement measurement = Measurement.builder()
                    .meter(meter)
                    .Kwh(deliveredMeasureDto.getValue())
                    .dateTime(deliveredMeasureDto.getDateTime())
                    .build();
            return measurementRepository.save(measurement);
        }else {
            throw new ResourceDoesNotExistException("Meter doesn't exist");
        }
    }

    public Measurement getMeasurementById(Integer id) throws ResourceDoesNotExistException {
        return measurementRepository.findById(id).orElseThrow(()->new ResourceDoesNotExistException("Measurement doesn't exist"));
    }


    public Page<Measurement> getAllMeasurementsByUser(Integer idUser, Pageable pageable) throws ResourceDoesNotExistException {
        User user = userService.getUserById(idUser);
        if(user != null) {
            return measurementRepository.findAllMeasurementsByUser(user, pageable);
        }else {
            throw new ResourceDoesNotExistException("User doesn't exist");
        }
    }

    public Page<ResultSet> getMeasurementByUserBetweenDate(Integer idUser, LocalDateTime since, LocalDateTime until, Pageable pageable) throws ResourceDoesNotExistException {
        User user = userService.getUserById(idUser);
        if(user != null) {
            return (Page<ResultSet>) measurementRepository.getMeasurementByUserBetweenDate(user.getUsername(), since, until,pageable);
        }else {
            throw new ResourceDoesNotExistException("User doesn't exist");
        }
    }
}

