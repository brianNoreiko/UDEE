package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.repository.MeterRepository;
import com.utn.UDEE.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.MeterUtilsTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MeterServiceTest {
    private static MeterRepository meterRepository;
    private static MeterService meterService;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUp(){
        meterRepository = mock(MeterRepository.class);
        userRepository = mock (UserRepository.class);
        meterService = new MeterService(meterRepository,userRepository);
    }

    @AfterEach
    public void after(){
        reset(userRepository);
        reset(meterRepository);
    }

    @Test
    public void getAllMetersOk(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(meterRepository.findAll(pageable)).thenReturn(aMeterPage());
        Page<Meter> meterPage = meterService.getAllMeters(pageable);
        //Then
        assertEquals(aMeterPage(),meterPage);
        verify(meterRepository,times(1)).findAll(pageable);
    }

    @Test
    public void getAllMetersNC(){ // NC == No Content
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(meterRepository.findAll(pageable))
                .thenReturn(aMeterEmptyPage());
        Page<Meter> meterPage = meterService.getAllMeters(pageable);
        //Then
        assertEquals(aMeterEmptyPage(),meterPage);
        verify(meterRepository,times(1)).findAll(pageable);
    }

    @Test
    public void getMeterByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(meterRepository.findById(id))
                .thenReturn(Optional.of(aMeter()));

        Meter meter = meterService.getMeterById(id);
        //Then
        verify(meterRepository, times(1)).findById(id);
        assertEquals(aMeter(),meter);
    }

    @Test
    public void getMeterByIdError() {
        //Given
        Integer id = anyInt();
        //When
        when(meterRepository.findById(1)).thenReturn(Optional.empty());
        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> meterService.getMeterById(id));
        verify(meterRepository,times(1)).findById(id);
    }

    @Test
    public void addNewMeterOK(){
        //Given
        Meter aMeter = aMeter();
        //When
        try{
            when(meterRepository.existsById(aMeter.getSerialNumber())).thenReturn(false);
            when(meterRepository.save(aMeter)).thenReturn(aMeter);
            Meter meter = meterService.addMeter(aMeter);

            //Then
            Assert.assertEquals(aMeter(),meter);
            verify(meterRepository,times(1)).existsById(aMeter.getSerialNumber());
            verify(meterRepository,times(1)).save(aMeter);
        } catch (ResourceAlreadyExistException e) {
            fail(e);
        }
    }

    @Test
    public void addNewMeterAlreadyExist(){
        //Given
        Meter aMeter = aMeter();
        //When
        when(meterRepository.existsById(aMeter.getSerialNumber())).thenReturn(true);

        //Then
        assertThrows(ResourceAlreadyExistException.class,() -> meterService.addMeter(aMeter));
        verify(meterRepository,times(1)).existsById(aMeter.getSerialNumber());
        verify(meterRepository,times(0)).save(aMeter);
    }

    @Test
    public void deleteMeterByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idMeter = anyInt();
        //When
        try {
            when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
            when(aMeter() == null).thenReturn(false);
            when(aMeter().getAddress() == null).thenReturn(true);
            doNothing().when(meterRepository).deleteById(aMeter().getSerialNumber());

            meterService.deleteMeterById(idMeter);
            //Then
            verify(meterService,times(1)).getMeterById(idMeter);
            verify(meterRepository,times(1)).deleteById(idMeter);
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteMeterDenied();
        }
    }

    @Test
    public void deleteMeterByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idMeter = anyInt();
        //When
        try {
            when(meterService.getMeterById(idMeter)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class, () -> meterService.deleteMeterById(idMeter));
            verify(meterService, times(1)).getMeterById(idMeter);
            verify(meterRepository, times(0)).deleteById(idMeter);

        }catch (ResourceDoesNotExistException e){

            deleteMeterDenied();
        }
    }

    @Test
    public void updateMeterOK() {
        //Given
        Integer idMeterToUp = anyInt();
        Meter aMeter = aMeter();
        //When
        try {
            when(meterService.getMeterById(idMeterToUp)).thenReturn(aMeter);
            when(meterRepository.save(aMeter)).thenReturn(aMeter);

            Meter meter = meterService.updateMeter(idMeterToUp,aMeter);
            //Then
            assertEquals(aMeter(),meter);
            verify(meterService, times(1)).getMeterById(idMeterToUp);
            verify(meterRepository, times(1)).save(meter);
        }
        catch (ResourceDoesNotExistException | PrimaryKeyViolationException | ResourceAlreadyExistException e){
            updateMeterDenied();
        }
    }

    @SneakyThrows
    @Test
    public void updateMeterDenied(){
        Meter meter = aMeter();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->meterService.updateMeter(1,meter));
    }

    @SneakyThrows
    @Test
    public void deleteMeterDenied(){
        Integer idMeter = anyInt();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->meterService.deleteMeterById(idMeter));
    }
}
