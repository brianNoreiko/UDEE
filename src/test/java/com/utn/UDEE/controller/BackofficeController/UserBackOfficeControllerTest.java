package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.backofficeController.AddressBackOfficeController;
import com.utn.UDEE.controller.backofficeController.UserBackOfficeController;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.service.AddressService;
import com.utn.UDEE.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddressDto;
import static com.utn.UDEE.utils.UserUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserBackOfficeController.class)
public class UserBackOfficeControllerTest extends AbstractController {

    @MockBean
    private static UserService userService;
    private static UserBackOfficeController userBackOfficeController;
    private static ConversionService conversionService;

    @BeforeAll
    public static void setUp(){
        userService = mock(UserService.class);
        conversionService = mock(ConversionService.class);
        userBackOfficeController = mock(UserBackOfficeController.class);
    }

    @Test
    public void getAllUsers() throws Exception {

        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(HttpStatus.OK.value(), resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void addUser() throws Exception{
        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
        .post("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content(aUserJSON()))
                .andExpect(status().isCreated());

        assertEquals(HttpStatus.CREATED.value(), resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getUsersById() throws Exception{
        when(userService.getUserById(1)).thenReturn(aUser());
        when(conversionService.convert(aUser(), UserDto.class)).thenReturn(aUserDto());

        ResponseEntity<UserDto> responseEntity = userBackOfficeController.getUserById(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aUserDto(), responseEntity.getBody());
    }
}
