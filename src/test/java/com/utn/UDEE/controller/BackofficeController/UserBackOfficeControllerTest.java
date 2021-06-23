package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.backofficeController.UserBackOfficeController;
import com.utn.UDEE.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.utn.UDEE.utils.UserUtilsTest.aUserJSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserBackOfficeController.class)
public class UserBackOfficeControllerTest extends AbstractController {

    @MockBean
    private UserService userService;
    private UserBackOfficeControllerTest userBackOfficeControllerTest;

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



}
