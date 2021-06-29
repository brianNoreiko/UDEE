package com.utn.UDEE.controller.BackofficeController;

import com.utn.UDEE.AbstractController;
import com.utn.UDEE.controller.backofficeController.UserBackOfficeController;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.UserDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.UserService;
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

import static com.utn.UDEE.utils.UserUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class UserBackOfficeControllerTest extends AbstractController {

    private static UserService userService;
    private static UserBackOfficeController userBackOfficeController;
    private static ConversionService conversionService;

    @BeforeAll
    public static void setUp(){
        userService = mock(UserService.class);
        conversionService = mock(ConversionService.class);
        userBackOfficeController = new UserBackOfficeController(userService,conversionService);
    }

    @AfterEach
    public void after(){
        reset(userService);
        reset(conversionService);
    }

    @Test
    public void getAllUsersOK() throws Exception {
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(userService.getAllUsers(pageable)).thenReturn(aUserPage());
        when(conversionService.convert(aUserPage(),UserDto.class)).thenReturn(aUserDto());

        ResponseEntity<List<UserDto>> responseEntity = userBackOfficeController.getAllUsers(page,size);
        //Then
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(aUserDtoPage().getContent().size(),responseEntity.getBody().size());
        verify(userService,times(1)).getAllUsers(pageable);
        verify(conversionService,times(1)).convert(aUserPage(), UserDto.class);
    }

    @Test
    public void getAllUsersNC() throws Exception {  //NC == No Content
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(userService.getAllUsers(pageable)).thenReturn(aUserEmptyPage());

        ResponseEntity<List<UserDto>> responseEntity = userBackOfficeController.getAllUsers(page,size);
        //Then
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertEquals(0,responseEntity.getBody().size());
        verify(userService,times(1)).getAllUsers(pageable);
        verify(conversionService,times(0)).convert(aUserPage(), UserDto.class);
    }

    @Test
    public void getUserByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = 1;
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(conversionService.convert(aUser(),UserDto.class)).thenReturn(aUserDto());

        ResponseEntity<UserDto> responseEntity = userBackOfficeController.getUserById(idUser);
        //Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aUserDto(), responseEntity.getBody());
        verify(userService,times(1)).getUserById(idUser);
        verify(conversionService,times(1)).convert(aUser(),UserDto.class);
    }

    @Test
    public void addUserOK() throws Exception{
        //Given
        User user = aUser();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            //When
            when(userService.addUser(user)).thenReturn(user);
            ResponseEntity<Response> responseEntity = userBackOfficeController.addUser(aUser());
            //Then
            Assert.assertEquals(EntityURLBuilder.buildURL("users", user.getId()).toString(),responseEntity.getHeaders().get("Location").get(0));
            Assert.assertEquals(HttpStatus.CREATED.value(),responseEntity.getStatusCode().value());
            verify(userService,times(1)).addUser(user);
        }
        catch (ResourceAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUsersByIdOK() throws Exception{
        //Given
        Integer idUser = 1;
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(conversionService.convert(aUser(), UserDto.class)).thenReturn(aUserDto());

        ResponseEntity<UserDto> responseEntity = userBackOfficeController.getUserById(1);
        //Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(aUserDto(), responseEntity.getBody());
        verify(userService,times(1)).getUserById(idUser);
        verify(conversionService,times(1)).convert(aUser(),UserDto.class);
    }
}
