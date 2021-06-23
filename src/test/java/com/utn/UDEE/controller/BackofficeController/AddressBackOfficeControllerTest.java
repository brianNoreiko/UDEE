package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.androidAppController.InvoiceAppController;
import com.utn.UDEE.controller.backofficeController.AddressBackOfficeController;
import com.utn.UDEE.models.dto.AddressDto;
import com.utn.UDEE.models.dto.InvoiceDto;
import com.utn.UDEE.service.AddressService;
import com.utn.UDEE.service.InvoiceService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.AddressUtilsTest.aAddressDto;
import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoice;
import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoiceDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void getAllAddress() throws Exception {

        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(HttpStatus.OK.value(),resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAddressById() throws Exception{
        when(conversionService.convert(aAddress(), AddressDto.class)).thenReturn(aAddressDto());

        ResponseEntity<AddressDto> responseEntity = addressBackOfficeController.getAddressById(1);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}
