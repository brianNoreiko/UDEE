package com.utn.UDEE.controller;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.backofficeController.UserBackOfficeController;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.utn.UDEE.utils.UserUtilsTest.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserBackOfficeController.class)
public class UserBackOfficeControllerTest extends AbstractController {

    @MockBean
    private UserService userService;
    private UserBackOfficeController userBackOfficeController;

    @Test
    public void getAllUsers() throws Exception {

        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(HttpStatus.OK.value(), resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void addAddressToClient() throws Exception{
        when(userService.addAddressToClient(1,1)).thenReturn(aUser());
        ResponseEntity<Response> responseEntity = userBackOfficeController.addAddressToClient(1,1);
        assertEquals(HttpStatus.ACCEPTED.value(), responseEntity.getStatusCode().value());
    }

}
