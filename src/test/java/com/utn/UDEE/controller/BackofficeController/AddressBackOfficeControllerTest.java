package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.backofficeController.AddressBackOfficeController;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.dto.AddressDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.AddressService;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static com.utn.UDEE.utils.AddressUtilsTest.*;
import static com.utn.UDEE.utils.EntityResponse.messageResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


public class AddressBackOfficeControllerTest extends AbstractController {

    private static AddressService addressService;
    private static AddressBackOfficeController addressBackOfficeController;
    private static ConversionService conversionService;

    @BeforeAll
    public static void setUp(){
        addressService = mock(AddressService.class);
        conversionService = mock(ConversionService.class);
        addressBackOfficeController = new AddressBackOfficeController(addressService, conversionService);
    }

    @AfterEach
    public void after(){
        reset(addressService);
        reset(conversionService);
    }

    @Test
    public void getAddressByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(conversionService.convert(addressService.getAddressById(id), AddressDto.class)).thenReturn(aAddressDto());
        ResponseEntity<AddressDto> responseEntity = addressBackOfficeController.getAddressById(id);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aAddressDto(), responseEntity.getBody());
        verify(conversionService,times(1)).convert(addressService.getAddressById(id), AddressDto.class);
    }

    @Test
    public void getAllAddressesOK(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(addressService.getAllAddresses(pageable)).thenReturn(aAddressPage());
        when(conversionService.convert(aAddress(), AddressDto.class)).thenReturn(aAddressDto());

        ResponseEntity<List<AddressDto>> responseEntity = addressBackOfficeController.getAllAddresses(page,size);

        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(addressService,times(1)).getAllAddresses(pageable);
        verify(conversionService,times(1)).convert(aAddress(),AddressDto.class);
    }

    @Test
    public void getAllAddressesNC(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(addressService.getAllAddresses(pageable)).thenReturn(aAddressEmptyPage());

        ResponseEntity<List<AddressDto>> responseEntity = addressBackOfficeController.getAllAddresses(page,size);

        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(addressService,times(1)).getAllAddresses(pageable);
        verify(conversionService,times(0)).convert(aAddressPage(),AddressDto.class);
    }


    @Test
    public void addNewAddressCreated() {
        //Given
        Address address = aAddress();
        //When
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            when(addressService.addNewAddress(address)).thenReturn(address);
            ResponseEntity<Response> responseEntity = addressBackOfficeController.addNewAddress(address);

            Assert.assertEquals(EntityURLBuilder.buildURL("addresses", address.getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
            verify(addressService,times(1)).addNewAddress(address);
        }
        catch (ResourceAlreadyExistException | ResourceDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateAddress() {
        //Given
        Integer idToUp = 1;
        Address address = aAddress();
        try {
            //When
            when(addressService.updateAddress(idToUp,address)).thenReturn(aAddress());

            ResponseEntity<Response> responseEntity = addressBackOfficeController.updateAddress(idToUp,address);
            //Then
            Assert.assertEquals(HttpStatus.ACCEPTED.value(),responseEntity.getStatusCode().value());
            Assert.assertEquals(messageResponse("Address updated successfully"),responseEntity.getBody());
            verify(addressService,times(1)).updateAddress(idToUp,address);
        }
        catch (ResourceAlreadyExistException | PrimaryKeyViolationException | ResourceDoesNotExistException e) {
            fail(e);
        }
    }

    @Test
    public void addMeterToAddress() throws Exception {
        //Given
        Integer idAddress = 1;
        Integer idMeter = 1;
        //When
        when(addressService.addMeterToAddress(idAddress, idMeter)).thenReturn(aAddress());

        ResponseEntity<Response> responseEntity = addressBackOfficeController.addMeterToAddress(idAddress,idMeter);
        //Then
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(addressService,times(1)).addMeterToAddress(idAddress,idMeter);
    }

    @Test
    public void addRateToAddress() throws Exception{
        //Given
        Integer idAddress = 1;
        Integer idRate = 1;
        //when
        when(addressService.addRateToAddress(idAddress,idRate)).thenReturn(aAddress());

        ResponseEntity<Response> responseEntity = addressBackOfficeController.addRateToAddress(idAddress,idRate);
        //Then
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(addressService,times(1)).addRateToAddress(idAddress,idRate);
    }

    @Test
    public void deleteAddressById() throws Exception{
        //Given
        Integer idAddress = 1;
        //When
        doNothing().when(addressService).deleteAddressById(idAddress);

        ResponseEntity<Object> responseEntity = addressBackOfficeController.deleteAddressById(idAddress);
        //Then
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(addressService,times(1)).deleteAddressById(idAddress);
    }



}
