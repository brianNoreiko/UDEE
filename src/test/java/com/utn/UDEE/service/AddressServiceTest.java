package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.AddressRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.*;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static com.utn.UDEE.utils.RateUtilsTest.aRate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class AddressServiceTest {
    private static AddressRepository addressRepository;
    private static MeterService meterService;
    private static RateService rateService;
    private static AddressService addressService;

    @BeforeAll
    public static void setUp(){
        addressRepository = mock(AddressRepository.class);
        meterService = mock(MeterService.class);
        rateService = mock(RateService.class);
        addressService = new AddressService(addressRepository,meterService,rateService);
    }

    @AfterEach
    public void after(){
        reset(addressRepository);
        reset(meterService);
        reset(rateService);
    }

    @Test
    public void getAllAddressesOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(addressRepository.findAll(pageable)).thenReturn(aAddressPage());
        Page <Address> addressPage = addressService.getAllAddresses(pageable);
        //Then
        assertEquals(aAddressPage(),addressPage);
        verify(addressRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getAllAddressesNC(){ // NC == No Content
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(addressRepository.findAll(pageable))
                .thenReturn(aAddressEmptyPage());

        Page<Address> addressPage = addressService.getAllAddresses(pageable);
        //Then
        verify(addressRepository, times(1)).findAll(pageable);
        assertEquals(aAddressEmptyPage(), addressPage);
    }

    @Test
    public void getAddressByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(addressRepository.findById(id))
                .thenReturn(Optional.of(aAddress()));

        Address address = addressService.getAddressById(id);
        //Then
        verify(addressRepository, times(1)).findById(id);
        assertEquals(aAddress(),address);
    }

    @Test
    public void getAddressByIdError() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(addressRepository.findById(1)).thenReturn(Optional.empty());
        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> addressService.getAddressById(id));
        verify(addressRepository,times(1)).findById(id);
    }

   @Test
    public void addNewAddressOK(){
        //Given
        Address aAddress = aAddress();
        //When
        try {
            when(addressRepository.existsById(aAddress.getId())).thenReturn(false);

            when(addressRepository.save(aAddress)).thenReturn(aAddress);

            Address address = addressService.addNewAddress(aAddress);

            //Then
            Assert.assertEquals(aAddress(),address);
            verify(addressRepository,times(1)).existsById(aAddress.getId());
            verify(addressRepository,times(1)).save(aAddress);
        }catch (ResourceAlreadyExistException | ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void addAddressAlreadyExists()  {
        //Given
        Address aAddress = aAddress();
        //When
        when(addressRepository.existsById(aAddress.getId())).thenReturn(true);
        //Then
        assertThrows(ResourceAlreadyExistException.class, () -> addressService.addNewAddress(aAddress()));
        verify(addressRepository,times(1)).existsById(aAddress.getId());
        verify(addressRepository,times(0)).save(aAddress);
    }

   @Test
    public void updateAddressOK() {
        //Given
         Integer idAddressToUp = anyInt();
         Address aAddress = aAddress();
         //When
        try {
            when(addressService.getAddressById(idAddressToUp)).thenReturn(aAddress());
            when(addressRepository.save(aAddress)).thenReturn(aAddress);

            Address address = addressService.updateAddress(idAddressToUp,aAddress);
            //Then
            assertEquals(aAddress(),address);
            verify(addressService, times(1)).getAddressById(idAddressToUp);
            verify(addressRepository, times(1)).save(address);
        }
        catch (ResourceDoesNotExistException | PrimaryKeyViolationException | ResourceAlreadyExistException e){
            updateAddressDenied();
        }
    }

    @Test
    public void addMeterToAddressOK() throws ResourceDoesNotExistException {
        //Given
        Integer idMeter = 1;
        Integer idAddress = 1;
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
            when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
            when(addressRepository.save(aAddress())).thenReturn(aAddress());

            Address address = addressService.addMeterToAddress(idAddress, idMeter);

            //Then
            verify(addressRepository, times(1)).save(aAddress());
            verify(meterService, times(1)).getMeterById(idMeter);
            verify(addressService, times(1)).getAddressById(idAddress);
            assertEquals(aAddress(), address);
        }catch (ResourceDoesNotExistException e){
            meterToAddressDenied();
        }
    }

    @Test
    public void addMeterToAddressNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idMeter = 1;
        Integer idAddress = 1;
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(null);
            when(meterService.getMeterById(idMeter)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class, () -> addressService.addMeterToAddress(idAddress, idMeter));
            verify(addressService, times(1)).getAddressById(idAddress);
            verify(meterService, times(1)).getMeterById(idMeter);
            verify(addressRepository, times(0)).save(aAddress());
        }catch (ResourceDoesNotExistException e){
            meterToAddressDenied();
        }
    }

    @Test
    public void addRateToAddressOk(){
        //Given
        Integer idAddress = 1;
        Integer idRate = 1;
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
            when(rateService.getRateById(idRate)).thenReturn(aRate());
            when(addressRepository.save(aAddress())).thenReturn(aAddress());

            Address address = addressService.addRateToAddress(idAddress,idRate);
            //Then
            verify(addressService, times(1)).getAddressById(idAddress);
            verify(rateService, times(1)).getRateById(idRate);
            verify(addressRepository, times(1)).save(aAddress());
            assertEquals(aAddress(),address);
        }
        catch (ResourceDoesNotExistException e){
            rateToAddressDenied();
        }
    }

    @Test
    public void addRateToAddressNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = 1;
        Integer idRate = 1;
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(null);
            when(rateService.getRateById(idRate)).thenReturn(null);
            //Then
            verify(addressService, times(1)).getAddressById(idAddress);
            verify(rateService, times(1)).getRateById(idRate);
            verify(addressRepository, times(0)).save(aAddress());
            assertThrows(ResourceDoesNotExistException.class, () -> addressService.addRateToAddress(idAddress, idRate));
        }catch (ResourceDoesNotExistException e){
            rateToAddressDenied();
        }
    }

    @Test
    public void deleteAddressByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idAddress = anyInt();
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
            when(aAddress().getMeter()).thenReturn(null);
            doNothing().when(addressRepository).deleteById(idAddress);

            addressService.deleteAddressById(idAddress);
            //Then
            verify(addressService,times(1)).getAddressById(idAddress);
            verify(addressRepository,times(1)).deleteById(idAddress);
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteAddressDenied();
        }
    }

    @Test
    public void deleteAddressByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idAddress = anyInt();
        //When
        try {
            when(addressService.getAddressById(idAddress)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class,() -> addressService.deleteAddressById(idAddress));
            verify(addressService,times(1)).getAddressById(idAddress);
            verify(addressRepository,times(0)).deleteById(idAddress);
        }catch (ResourceDoesNotExistException e){
            deleteAddressDenied();
        }
    }

    @SneakyThrows
    @Test
    public void deleteAddressDenied(){
        Integer idAddress = anyInt();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->addressService.deleteAddressById(idAddress));
    }

    @SneakyThrows
    @Test
    public void updateAddressDenied(){
        Address address = aAddress();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->addressService.updateAddress(1,address));
    }

    @SneakyThrows
    @Test
    public void meterToAddressDenied(){
        Address address = aAddress();
        Meter meter = aMeter();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->addressService.addMeterToAddress(address.getId(),meter.getSerialNumber()));
    }

    @SneakyThrows
    @Test
    public void rateToAddressDenied(){
        Address address = aAddress();
        Rate rate = aRate();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->addressService.addMeterToAddress(address.getId(),rate.getId()));
    }
}

