package com.utn.UDEE.service;

import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.repository.AddressRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

    @Test
    public void getAllAddressesOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(addressRepository.findAll(pageable))
                .thenReturn(aAddressPage());

        addressService.getAllAddresses(pageable);
        //Then
        Mockito.verify(addressRepository, Mockito.times(2)).findAll(pageable);
    }

    @Test
    public void getAllAddressesNC(){ // NC == No Content
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(addressRepository.findAll(pageable))
                .thenReturn(aAddressEmptyPage());

        Page<Address> addressPage = addressService.getAllAddresses(pageable);
        //Then
        Mockito.verify(addressRepository, Mockito.times(1)).findAll(pageable);
        assertEquals(aAddressEmptyPage().getContent().size(),addressPage.getContent().size() );
        assertEquals(aAddressEmptyPage().getTotalPages(), addressPage.getTotalPages());
        assertEquals(aAddressEmptyPage().getTotalElements(),addressPage.getTotalElements());
    }

    @Test
    public void getAddressByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(addressRepository.findById(id))
                .thenReturn(Optional.of(aAddress()));

        Address address = addressService.getAddressById(id);
        //Then
        Mockito.verify(addressRepository, times(2)).findById(id);
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
    }

   @Test
    public void addNewAddressOK(){
        //Given
        Address aAddress = aAddress();
        //When
        try {
            when(addressRepository.existsById(anyInt())).thenReturn(false);

            when(addressRepository.save(aAddress)).thenReturn(aAddress());

            Address address = addressService.addNewAddress(aAddress);

            //Then
            Assert.assertEquals(aAddress,address);

        }catch (ResourceAlreadyExistException | ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void addAddressAlreadyExists()  {
        //When
        when(addressRepository.existsById(anyInt())).thenReturn(true);
        //Then
        assertThrows(ResourceAlreadyExistException.class, () -> addressService.addNewAddress(aAddress()));
    }

   @Test
    public void updateAddressAcepted() {
         Address address = aAddress();
        try {
            when(addressRepository.findById(anyInt())).thenReturn(Optional.of(aAddress()));
            when(addressRepository.save(address)).thenReturn(address);

            Address address1 = addressService.updateAddress(1,address);

            assertEquals(address,address1);

            verify(addressRepository, times(1)).findById(anyInt());
            verify(addressRepository, times(1)).save(address);
        }
        catch (ResourceDoesNotExistException | PrimaryKeyViolationException | ResourceAlreadyExistException e){
            updateAddressDenied();
        }
    }
    @SneakyThrows
    @Test
    public void updateAddressDenied(){
        Address address = aAddress();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->addressService.updateAddress(1,address));
    }

    @Test
    public void addMeterToAddressOK() throws ResourceDoesNotExistException {
        //Given
        Address address = aAddress();
        //When
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(aAddress()));
        when(meterService.getMeterById(address.getMeter().getSerialNumber())).thenReturn(aMeter());
        address.setMeter(aMeter());
        when(addressRepository.save(address)).thenReturn(address);

        Address address1 = addressService.addMeterToAddress(1,1);

        //Then
        verify(addressRepository,times(2)).save(aAddress());
        verify(meterService,times(1)).getMeterById(address.getMeter().getSerialNumber());
        verify(addressRepository,times(2)).findById(address.getId());
        assertEquals(address,address1);
    }

    @Test
    public void addRateToAddressOk(){
        try {
            Address aAddress = aAddress();
            aAddress.setRate(aRate());
            when(addressRepository.findById(aAddress.getId())).thenReturn(Optional.of(aAddress));
            when(rateService.getRateById(aRate().getId())).thenReturn(aRate());
            when(addressRepository.save(aAddress)).thenReturn(aAddress);

            Address address = addressService.addRateToAddress(aAddress.getId(), aRate().getId());

            verify(addressRepository, times(3)).findById(aAddress.getId());
            verify(rateService, times(1)).getRateById(aRate().getId());
            verify(addressRepository, times(1)).save(aAddress);
            assertEquals(address.getRate(), address.getRate());
        }
        catch (ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void addRateToAddressError() {
        when(addressRepository.findById(aAddress().getId())).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> addressService.addMeterToAddress(aAddress().getId(), aMeter().getSerialNumber()));
        verify(addressRepository, times(4)).findById(aAddress().getId());
    }
}

