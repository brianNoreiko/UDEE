package com.utn.UDEE.service;

import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.repository.AddressRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.*;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
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
        Mockito.verify(addressRepository, Mockito.times(1)).findAll(pageable);
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
        Mockito.verify(addressRepository, times(1)).findById(id);
        assertEquals(aAddress(),address);
    }

    @Test
    public void getAddressByIdError() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(addressRepository.findById(1)).thenReturn(Optional.empty());
        Address address = addressService.getAddressById(id);
        //Then
        verify(addressRepository,times(1)).findById(id);
        assertEquals(Optional.empty(),address);
        assertThrows(ResourceDoesNotExistException.class, () -> addressService.getAddressById(id));
        // "com.utn.UDEE.exception.ResourceDoesNotExistException: Address doesn't exist"  ????
    }

    @Test
    public void addNewAddressOK(){
        //Given
        Address aAddress = aAddress();
        //When
        try {
            when(addressService.getAddressById(aAddress.getId())).thenReturn(null);
            when(addressRepository.save(aAddress)).thenReturn(aAddress());

            Address address = addressService.addNewAddress(aAddress);

            //Then
            verify(addressRepository, times(1)).save(address);
            verify(addressService, times(1)).getAddressById(address.getId());
            verify(addressService, times(1)).addNewAddress(address);

        }catch (ResourceAlreadyExistException | ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void addAddressAlreadyExists()  {
        //When
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(aAddress()));
        //Then
        assertThrows(ResourceAlreadyExistException.class, () -> addressService.addNewAddress(aAddress()));
        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times( 0)).save(aAddress());
    }

    @Test
    public void updateAddressAcepted() {
        Address aAddress = aAddress();
        try {
            when(addressRepository.findById(anyInt())).thenReturn(Optional.of(aAddress()));
            when(addressRepository.save(aAddress())).thenReturn(aAddress());

            Address address = addressService.updateAddress(aAddress.getId(),aAddress);

            assertEquals(aAddress,address);

            verify(addressRepository, times(1)).save(aAddress);
        }
        catch (ResourceDoesNotExistException | PrimaryKeyViolationException | ResourceAlreadyExistException e){
            fail(e);
        }
    }

    @Test
    public void addMeterToAddressOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        Integer idMeter = anyInt();
        Address address = aAddress();
        //When
        when(addressService.getAddressById(id)).thenReturn(aAddress());
        when(meterService.getMeterById(idMeter)).thenReturn(aMeter());
        address.setMeter(aMeter());

    }
}
