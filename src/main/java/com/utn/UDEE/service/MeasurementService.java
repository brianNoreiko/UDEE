package com.utn.UDEE.service;

import com.utn.UDEE.exception.AccessNotAllowedException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.*;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.repository.MeasurementRepository;
import com.utn.UDEE.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static com.utn.UDEE.utils.Utils.userPermissionCheck;
import static java.util.Objects.isNull;

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

    public ClientConsuption getConsumptionByMeterAndBetweenDate(Integer idMeter, Integer idQueryUser, LocalDateTime since, LocalDateTime until) throws ResourceDoesNotExistException, AccessNotAllowedException {
        Meter meter = meterService.getMeterById(idMeter);
        User queryUser = userService.getUserById(idQueryUser);
        User user = meter.getAddress().getUser();

        double totalConsumptionKw = 0.0;
        double totalConsumptionMoney = 0.0;
        int quantityMeasurements = 0;


        userPermissionCheck(queryUser,user);

        List<Measurement> measurements = measurementRepository.findAllByMeterAndDateBetween(meter,since,until);

        double firstMeasurement = isNull(measurements) ? measurements.get(0).getKwhPrice() : 0.0;

        if(!measurements.isEmpty()) {

            totalConsumptionKw = measurements.get(measurements.size() - 1).getKwh() - measurements.get(0).getKwh();

            if(measurements.size() == 1){
                totalConsumptionKw = measurements.get(0).getKwh();
            }

            for(Measurement m : measurements) {
                totalConsumptionMoney += m.getKwhPrice();
            }

            quantityMeasurements = measurements.size();
        }

        return ClientConsuption.builder()
                .consumptionKw(totalConsumptionKw)
                .consumptionCost(totalConsumptionMoney - firstMeasurement)
                .measurementsCount(quantityMeasurements)
                .since(since)
                .until(until)
                .user(new UserDto(user.getId(),user.getName(), user.getLastname(), user.getUsername(),user.getUserType()))
                .build();
    }


    public Page<Measurement> getAllByMeterAndBetweenDate(Integer idMeter, Integer idUser, LocalDateTime since, LocalDateTime until, Pageable pageable) throws AccessNotAllowedException, ResourceDoesNotExistException {
        Meter meter = meterService.getMeterById(idMeter);
        User user = userService.getUserById(idUser);

        if(userService.containsMeter(user,meter) || user.getUserType().equals(UserType.EMPLOYEE)) {
            return measurementRepository.findAllByMeterAndDateBetween(meter,since,until,pageable);
        }
        else {
            throw new AccessNotAllowedException("You have not access to this resource");
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
        boolean meterExist = meterRepository.existsById(deliveredMeasureDto.getSerialNumber());
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

