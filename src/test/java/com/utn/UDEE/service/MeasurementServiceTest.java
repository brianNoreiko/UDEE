package com.utn.UDEE.service;

import com.utn.UDEE.exception.AccessNotAllowedException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;
import com.utn.UDEE.models.dto.DeliveredMeasureDto;
import com.utn.UDEE.repository.MeasurementRepository;
import com.utn.UDEE.repository.MeterRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
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
import static com.utn.UDEE.utils.UserUtilsTest.aUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class MeasurementServiceTest {
    private static MeasurementRepository measurementRepository;
    private static MeterRepository meterRepository;
    private static MeterService meterService;
    private static AddressService addressService;
    private static UserService userService;
    private static MeasurementService measurementService;

    @BeforeAll
    public static void setUp() {
        measurementRepository = mock(MeasurementRepository.class);
        meterRepository = mock(MeterRepository.class);
        meterService = mock(MeterService.class);
        addressService = mock(AddressService.class);
        userService = mock(UserService.class);
        measurementService = new MeasurementService(measurementRepository,meterRepository, meterService, addressService, userService);
    }

    @AfterEach
    public void after() {
        reset(measurementRepository);
        reset(meterRepository);
        reset(meterService);
        reset(addressService);
        reset(userService);
    }

    @Test
    public void getAllByMeterAndBetweenDateOK() throws ResourceDoesNotExistException, AccessNotAllowedException {
        User user = aUser();
        user.setUserType(UserType.EMPLOYEE);
        //Given
        Integer idMeter = anyInt();
        Integer idUser  = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        try {
            when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
            when(userService.getUserById(idUser)).thenReturn(user);

            when(measurementRepository.findAllByMeterAndDateBetween(aMeter(), since, until, pageable)).thenReturn(aMeasurementPage());

            Page<Measurement> measurementPage = measurementService.getAllByMeterAndBetweenDate(idMeter, aUser().getId(), since, until, pageable);
            //Then
            assertEquals(aMeasurementPage().getTotalElements(),measurementPage.getTotalElements());
            assertEquals(aMeasurementPage().getTotalPages(), measurementPage.getTotalElements());
            assertEquals(aMeasurementPage().getContent().size(),measurementPage.getContent().size());
            verify(meterService, times(1)).getMeterById(idMeter);
            verify(measurementRepository, times(1)).findAllByMeterAndDateBetween(aMeter(), since, until, pageable);
        }catch (ResourceDoesNotExistException | AccessNotAllowedException e){
            fail(e);
        }
    }

    @Test
    public void getAllByMeterAndDateBetweenRestrict() {
        User user = aUser();
        user.setUserType(UserType.CLIENT);
        //Given
        Integer idMeter = 1;
        Integer idUser  = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        try {
            when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
            when(userService.getUserById(idUser)).thenReturn(user);

            when(measurementRepository.findAllByMeterAndDateBetween(aMeter(), since, until, pageable)).thenReturn(aMeasurementPage());


            //Then
            assertThrows(AccessNotAllowedException.class,() -> {
                measurementService.getAllByMeterAndBetweenDate(idMeter,idUser, since,until, pageable);
            } );
            verify(meterService,times(1)).getMeterById(1);
            verify(userService,times(1)).getUserById(1);
            verify(measurementRepository,times(0)).findAllByMeterAndDateBetween(aMeter(),since,until,pageable);
        }catch (ResourceDoesNotExistException e){
            fail(e);
        }
    }

    /*@Test
    public void getAllByMeterAndBetweenDateNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idMeter = anyInt();
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(1, 1);
        //When
        when(meterService.getMeterById(idMeter)).thenReturn();

        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> measurementService.getAllByMeterAndBetweenDate(idMeter,aUser().getId(), since, until, pageable));
        verify(meterService, times(1)).getMeterById(idMeter);
        verify(measurementRepository, times(0)).getAllByMeterAndDateBetween(aMeter(), since, until, pageable);
    }*/

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
    public void addMeasurementOK() {
        //Given
        DeliveredMeasureDto deliveredMeasureDto = aDeliveredMeasureDto();
        //When
        try {
            Measurement measurement = aMeasurement();
            when(meterRepository.existsById(deliveredMeasureDto.getSerialNumber())).thenReturn(true);
            when(meterService.getMeterById(deliveredMeasureDto.getSerialNumber())).thenReturn(aMeter());
            when(measurementRepository.save(any(Measurement.class))).thenReturn(measurement);

            Measurement savedMeasurement = measurementService.addMeasurement(deliveredMeasureDto);
            //Then
            assertEquals(measurement, savedMeasurement);
            verify(meterRepository,times(1)).existsById(deliveredMeasureDto.getSerialNumber());
            verify(meterService, times(1)).getMeterById(deliveredMeasureDto.getSerialNumber());
            verify(measurementRepository, times(1)).save(any(Measurement.class));
        } catch (ResourceDoesNotExistException e) {
            addMeasurementDenied();
        }
    }

    @Test
    public void addMeasurementNotExist() throws ResourceDoesNotExistException {
        //Given
        DeliveredMeasureDto deliveredMeasureDto = aDeliveredMeasureDto();
        //When
        try{
        when(meterRepository.existsById(deliveredMeasureDto.getSerialNumber())).thenReturn(false);
        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> measurementService.addMeasurement(deliveredMeasureDto));
        verify(meterRepository,times(1)).existsById(any());
        verify(meterService,times(0)).getMeterById(deliveredMeasureDto.getSerialNumber());
        verify(measurementRepository,times(0)).save(aMeasurement());
        } catch (ResourceDoesNotExistException e) {
            addMeasurementDenied();
        }
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

    @Test
    public void getAllMeasurementsByUserOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(measurementRepository.findAllMeasurementsByUser(aUser(),pageable)).thenReturn(aMeasurementPage());

        Page<Measurement> measurementPage = measurementService.getAllMeasurementsByUser(idUser,pageable);
        //Then
        assertEquals(aMeasurementPage(), measurementPage);
        verify(userService,times(1)).getUserById(idUser);
        verify(measurementRepository,times(1)).findAllMeasurementsByUser(aUser(),pageable);
    }

    @Test
    public void getAllMeasurementsByUserNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(measurementRepository.findAllMeasurementsByUser(aUser(),pageable)).thenReturn(aMeasurementEmptyPage());

        Page<Measurement> measurementPage = measurementService.getAllMeasurementsByUser(idUser,pageable);
        //Then
        assertEquals(aMeasurementEmptyPage(), measurementPage);
        verify(userService,times(1)).getUserById(idUser);
        verify(measurementRepository,times(1)).findAllMeasurementsByUser(aUser(),pageable);
    }

    @Test
    public void getAllMeasurementsByUserNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(null);
        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> measurementService.getAllMeasurementsByUser(idUser,pageable));
        verify(userService,times(1)).getUserById(idUser);
        verify(measurementRepository,times(0)).findAllMeasurementsByUser(aUser(),pageable);
    }

    @SneakyThrows
    @Test
    public void addMeasurementDenied(){
        DeliveredMeasureDto deliveredMeasureDto = aDeliveredMeasureDto();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->measurementService.addMeasurement(deliveredMeasureDto));
    }

}
