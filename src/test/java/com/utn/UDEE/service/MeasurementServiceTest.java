package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.repository.MeasurementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeSince;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeUntil;
import static com.utn.UDEE.utils.MeasurementUtilsTest.*;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class MeasurementServiceTest {
    private static MeasurementRepository measurementRepository;
    private static MeterService meterService;
    private static AddressService addressService;
    private static UserService userService;
    private static MeasurementService measurementService;

    @BeforeAll
    public static void setUp() {
        measurementRepository = mock(MeasurementRepository.class);
        meterService = mock(MeterService.class);
        addressService = mock(AddressService.class);
        userService = mock(UserService.class);
        measurementService = new MeasurementService(measurementRepository, meterService, addressService, userService);
    }

    @AfterEach
    public void after() {
        reset(measurementRepository);
        reset(meterService);
        reset(addressService);
        reset(userService);
    }

    @Test
    public void getAllByMeterAndBetweenDateOK() throws ResourceDoesNotExistException {
        //Given
        Integer idMeter = anyInt();
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(meterService.getMeterById(idMeter)).thenReturn(aMeter());

        when(measurementRepository.getAllByMeterAndBetweenDate(aMeter(), since, until, pageable)).thenReturn(aMeasurementPage());

        Page<Measurement> measurementPage = measurementService.getAllByMeterAndBetweenDate(idMeter, since, until, pageable);
        //Then
        assertEquals(aMeasurementPage(), measurementPage);
        verify(meterService, times(1)).getMeterById(idMeter);
        verify(measurementRepository, times(1)).getAllByMeterAndBetweenDate(aMeter(), since, until, pageable);
    }

    @Test
    public void getAllByMeterAndBetweenDateNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idMeter = anyInt();
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(meterService.getMeterById(idMeter)).thenReturn(null);

        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> measurementService.getAllByMeterAndBetweenDate(idMeter, since, until, pageable));
        verify(meterService, times(1)).getMeterById(idMeter);
        verify(measurementRepository, times(0)).getAllByMeterAndBetweenDate(aMeter(), since, until, pageable);
    }

    @Test
    public void getMeasurementByAddressBetweenDateOK() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = anyInt();
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
        when(measurementRepository.getMeasurementByAddressBetweenDate(aAddress(), since, until, pageable)).thenReturn(aMeasurementPage());

        Page<Measurement> measurementPage = measurementService.getMeasurementByAddressBetweenDate(idAddress, since, until, pageable);
        //Then
        assertEquals(aMeasurementPage(), measurementPage);
        verify(addressService, times(1)).getAddressById(idAddress);
        verify(measurementRepository, times(1)).getMeasurementByAddressBetweenDate(aAddress(), since, until, pageable);
    }

    @Test
    public void getMeasurementByAddressBetweenDateNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = anyInt();
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(addressService.getAddressById(idAddress)).thenReturn(null);

        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> measurementService.getMeasurementByAddressBetweenDate(idAddress, since, until, pageable));
        verify(addressService, times(1)).getAddressById(idAddress);
        verify(measurementRepository, times(0)).getMeasurementByAddressBetweenDate(aAddress(), since, until, pageable);
    }

    @Test
    public void getAllMeasurementsOK() {
        //Given
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(measurementRepository.findAll(pageable)).thenReturn(aMeasurementPage());
        Page<Measurement> measurementPage = measurementService.getAllMeasurements(pageable);
        //Then
        assertEquals(aMeasurementPage(), measurementPage);
        verify(measurementRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getAllMeasurementsNC() { //NC == No Content
        //Given
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(measurementRepository.findAll(pageable)).thenReturn(aMeasurementEmptyPage());
        Page<Measurement> measurementPage = measurementService.getAllMeasurements(pageable);
        //Then
        assertEquals(aMeasurementEmptyPage(), measurementPage);
        verify(measurementRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getMeasurementByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idMe = anyInt();
        //When
        when(measurementRepository.findById(idMe)).thenReturn(Optional.of(aMeasurement()));
        Measurement measurement = measurementService.getMeasurementById(idMe);
        //Then
        assertEquals(aMeasurement(),measurement);
        verify(measurementRepository,times(1)).findById(idMe);
    }

    @Test
    public void getMeasurementByIdNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idMe = anyInt();
        //When
        when(measurementRepository.findById(idMe)).thenReturn(Optional.empty());
        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> measurementService.getMeasurementById(idMe));
        verify(measurementRepository,times(1)).findById(idMe);
    }

}
