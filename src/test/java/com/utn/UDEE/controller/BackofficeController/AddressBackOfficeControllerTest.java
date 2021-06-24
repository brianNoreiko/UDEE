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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
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
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AddressBackOfficeController.class)
public class AddressBackOfficeControllerTest extends AbstractController {

    @MockBean
    private static AddressService addressService;
    private static AddressBackOfficeController addressBackOfficeController;
    private static ConversionService conversionService;

    @BeforeAll
    public static void setUp(){
        addressService = mock(AddressService.class);
        conversionService = mock(ConversionService.class);
        addressBackOfficeController = new AddressBackOfficeController(addressService, conversionService);
    }

    @Test
    public void getAddressByIdOK() throws ResourceDoesNotExistException {
        when(addressService.getAddressById(1)).thenReturn(aAddress());
        when(conversionService.convert(aAddress(), AddressDto.class)).thenReturn(aAddressDto());

        ResponseEntity<AddressDto> responseEntity = addressBackOfficeController.getAddressById(1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aAddressDto(), responseEntity.getBody());
    }

    @Test
    public void addNewAddressCreated() {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(addressService.addNewAddress(aAddress())).thenReturn(aAddress());
            ResponseEntity<Response> responseEntity = addressBackOfficeController.addNewAddress(aAddress());

            Assert.assertEquals(EntityURLBuilder.buildURL("addresses", aAddress().getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
        }
        catch (ResourceAlreadyExistException | ResourceDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllAddressesOK(){
        Pageable pageable = PageRequest.of(1,5);
        Page<Address> addressPage = aAddressPage();
        when(addressService.getAllAddresses(pageable)).thenReturn(aAddressPage());
        when(conversionService.convert(aAddressDto(), AddressDto.class)).thenReturn(aAddressDto());

        ResponseEntity<List<AddressDto>> responseEntity = addressBackOfficeController.getAllAddresses(1,5);

        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assert.assertEquals(addressPage.getContent().size(),responseEntity.getBody().size());
    }

    @Test
    public void getAllAddressesNC() { //NC == No Content
        Pageable pageable = PageRequest.of(1, 1);
        Page<Address> addressEmptyPage = aAddressEmptyPage();
        when(addressService.getAllAddresses(pageable)).thenReturn(addressEmptyPage);

        ResponseEntity<List<AddressDto>> responseEntity = addressBackOfficeController.getAllAddresses(1,1);

        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
    }

    @Test
    public void updateAddress() {
        try {
            Mockito.when(addressService.updateAddress(any(),any())).thenReturn(aAddress());

            ResponseEntity<Response> responseEntity = addressBackOfficeController.updateAddress(1,aAddress());

            Assert.assertEquals(HttpStatus.ACCEPTED.value(),responseEntity.getStatusCode().value());
            Assert.assertEquals(messageResponse("Address updated successfully"),responseEntity.getBody());

        }
        catch (ResourceAlreadyExistException | PrimaryKeyViolationException | ResourceDoesNotExistException e) {
            fail(e);
        }
    }

    @Test
    public void addMeterToAddress() throws Exception {
        when(addressService.addMeterToAddress(aAddress().getId(), aMeter().getSerialNumber())).thenReturn(aAddress());

        ResponseEntity<Response> responseEntity = addressBackOfficeController.addMeterToAddress(1,1);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void addRateToAddress() throws Exception{
        when(addressService.addRateToAddress(aAddress().getId(), aMeter().getSerialNumber())).thenReturn(aAddress());

        ResponseEntity<Response> responseEntity = addressBackOfficeController.addRateToAddress(1,1);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void deleteAddressById() throws Exception{
        doNothing().when(addressService).deleteAddressById(1);

        ResponseEntity<Object> responseEntity = addressBackOfficeController.deleteAddressById(1);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }



}
