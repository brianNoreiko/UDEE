package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.User;
import com.utn.UDEE.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.UserUtilsTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private static UserRepository userRepository;
    private static AddressService addressService;
    private static UserService userService;

    @BeforeAll
    public static void setUp(){
        userRepository = mock(UserRepository.class);
        addressService = mock(AddressService.class);
        userService = new UserService(userRepository,addressService);
    }

    @AfterEach
    public void after(){
        reset(userRepository);
        reset(addressService);
    }

    @Test
    public void loginOK(){
        //When
        when(userRepository.findByUsernameAndPassword(any(),any())).thenReturn(aUser());
        User user = userService.login(aUser().getUsername(),aUser().getPassword());
        //Then
        assertEquals(aUser().getId(),user.getId());
        assertEquals(aUser().getName(),user.getName());
        assertEquals(aUser().getLastname(),user.getLastname());
        assertEquals(aUser().getUsername(),user.getUsername());
        assertEquals(aUser().getUserType(),user.getUserType());
        assertEquals(aUser().getPassword(),user.getPassword());

        verify(userRepository,times(1)).findByUsernameAndPassword(aUser().getUsername(),aUser().getPassword());
    }

    @Test
    public void getUserByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = 1;
        //When
        when(userRepository.findById(idUser)).thenReturn(Optional.of(aUser()));
        User user = userService.getUserById(idUser);
        //Then
        assertEquals(aUser(),user);
        verify(userRepository, times(1)).findById(idUser);
    }

    @Test
    public void getUserByIdNotExist(){
        //Given
        Integer idUser = 1;
        //When
        when(userRepository.findById(idUser)).thenReturn(Optional.empty());

        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> userService.getUserById(idUser));
        verify(userRepository, times(1)).findById(idUser);
    }

    @Test
    public void getAllUsersOK(){
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(userRepository.findAll(pageable)).thenReturn(aUserPage());
        Page<User> userPage = userService.getAllUsers(pageable);
        //Then
        assertEquals(aUserPage(),userPage);
        verify(userRepository,times(1)).findAll(pageable);
    }


    @Test
    public void getAllUsersNC(){ //NC == No Content
        //Given
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        //When
        when(userRepository.findAll(pageable)).thenReturn(aUserEmptyPage());
        Page<User> userPage = userService.getAllUsers(pageable);
        //Then
        assertEquals(aUserEmptyPage(),userPage);
        verify(userRepository,times(1)).findAll(pageable);
    }

    @Test
    public void addUserOK(){
        //Given
        User aUser = aUser();
        try {
            //When
            when(userRepository.existsById(aUser.getId())).thenReturn(false);
            when(userRepository.save(aUser)).thenReturn(aUser);
            User user = userService.addUser(aUser);

            //Then
            assertEquals(aUser(),user);

            verify(userRepository, times(1)).existsById(aUser.getId());
            verify(userRepository, times(1)).save(user);
        }
        catch (ResourceAlreadyExistException e) {
            fail(e);
        }
    }

    @Test
    public void addUserError() {
        //Given
        User aUser = aUser();
        //When
        when(userRepository.existsById(aUser.getId())).thenReturn(true);

        //Then
        assertThrows(ResourceAlreadyExistException.class, () -> userService.addUser(aUser));

        verify(userRepository, times(1)).existsById(aUser.getId());
        verify(userRepository, times(0)).save(aUser);
    }

    @Test
    public void addAddressToClientOK(){
        //Given
        Integer idUser = 1;
        Integer idAddress = 1;
        try {

            //When
            when(userService.getUserById(idUser)).thenReturn(aUser());
            when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
            when(userRepository.save(aUser())).thenReturn(aUser());

            User user = userService.addAddressToClient(idUser,idAddress);

            //Then
            verify(userService,times(1)).getUserById(idUser);
            verify(addressService, times(1)).getAddressById(idAddress);
            verify(userRepository, times(1)).save(aUser());
            assertEquals(aUser(), user);
        }
        catch (ResourceDoesNotExistException e) {
            addressToClientDenied();
        }
    }

    @Test
    public void addAddressToClientFail() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = 1;
        Integer idAddress = 1;
        try {

            //When
            when(userService.getUserById(idUser)).thenReturn(null);
            when(addressService.getAddressById(idAddress)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class,() -> userService.addAddressToClient(idUser,idAddress));
            verify(userService,times(1)).getUserById(idUser);
            verify(addressService,times(1)).getAddressById(idAddress);
            verify(userRepository,times(0)).save(aUser());
        }catch (ResourceDoesNotExistException e){
            addressToClientDenied();
        }
    }

    @Test
    public void deleteUserByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idUser = 1;
        //When
        try {
            when(userService.getUserById(idUser)).thenReturn(aUser());
            when(aUser().getAddressList()).thenReturn(null);
            doNothing().when(userService).deleteById(idUser);

            userService.deleteById(idUser);
            //Then
            verify(userService, times(1)).getUserById(idUser);
            verify(userService, times(1)).deleteById(idUser);
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteUserDenied();
        }
    }

    @Test
    public void deleteUserByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idUser = anyInt();
        //When
        try {
            when(userService.getUserById(idUser)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class,() -> userService.deleteById(idUser));
            verify(userService,times(1)).getUserById(idUser);
            verify(userRepository,times(0)).deleteById(idUser);
        }catch (ResourceDoesNotExistException e){
            deleteUserDenied();
        }
    }

    @SneakyThrows
    @Test
    public void addressToClientDenied(){
        User user = aUser();
        Address address = aAddress();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->userService.addAddressToClient(user.getId(),address.getId()));
    }

    @SneakyThrows
    @Test
    public void deleteUserDenied(){
        Integer idUser = anyInt();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->userService.deleteById(idUser));
    }
}
