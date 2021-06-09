package com.utn.UDEE.controller;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserController.class)
public class UserControllerTest extends AbstractController {

    @MockBean
    private UserService userService;

    @Test
    public void getAllUsers() throws Exception {

        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(HttpStatus.OK.value(), resultActions.andReturn().getResponse().getStatus());
    }

}
