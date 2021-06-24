package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.responses.PaginationResponse;
import com.utn.UDEE.repository.AddressRepository;
import com.utn.UDEE.repository.UserRepository;
import com.utn.UDEE.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.UserUtilsTest.aUser;
import static com.utn.UDEE.utils.UserUtilsTest.aUserPage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private static UserRepository userRepository;
    private static AddressRepository addressRepository;
    private static UserService userService;

    @BeforeAll
    public static void setUp(){
        userRepository = mock(UserRepository.class);
        addressRepository = mock(AddressRepository.class);
        userService = new UserService(userRepository,addressRepository);
    }

    @Test
    public void loginOK(){
        //Given
        String email = anyString();
        String password = anyString();
        //When
        Mockito.when(userRepository.findByEmailAndPassword(email,password)).thenReturn(aUser());

        User user = userService.login(aUser().getEmail(),aUser().getPassword());
        //Then
        assertEquals(aUser(), user);
    }

    @Test
    public void getAllUsersOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userRepository.getAllUsers(pageable)).thenReturn(aUserPage());

        PaginationResponse<User> userPage = userService.getAllUsers(1,1);
        //Then
        assertEquals(aUserPage().getTotalElements(),userPage.getTotalElements());
        assertEquals(aUserPage().getTotalPages(), userPage.getTotalPages());
        verify(userRepository,times(1)).getAllUsers(pageable);
    }

    @Test
    public void addUserOK(){
        try {
            //When
            when(userRepository.findByUsernameOrEmail(aUser().getUsername(), aUser().getEmail())).thenReturn(null);
            when(userRepository.save(aUser())).thenReturn(aUser());
            User user = userService.addUser(aUser());

            //Then
            assertEquals(aUser(),user);

            verify(userRepository,Mockito.times(1)).findByUsernameOrEmail(aUser().getUsername(),aUser().getEmail());
            verify(userRepository,Mockito.times(1)).save(user);
        }
        catch (ResourceAlreadyExistException ex) {
            fail(ex);
        }
    }

    @Test
    public void addUserError() {
        //Given
        User user = aUser();
        //When
        when(userRepository.findByUsernameOrEmail(user.getUsername(),user.getEmail())).thenReturn(user);

        //Then
        assertThrows(ResourceAlreadyExistException.class, () -> userService.addUser(user));

        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(),user.getEmail());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void addAddressToClient(){
        try {
            User user = aUser();
            Address aAddress = aAddress();
            user.getAddressList().add(aAddress);
            //When
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(aUser()));
            when(addressRepository.findById(aAddress.getId())).thenReturn(Optional.of(aAddress()));
            when(userRepository.save(user)).thenReturn(user);

            User userReturned = userService.addAddressToClient(user.getId(),aAddress.getId());

            //Then
            verify(userRepository,times(1)).findById(user.getId());
            verify(addressRepository, times(1)).findById(aAddress.getId());
            verify(userRepository, times(1)).save(user);
            assertEquals(user, userReturned);
        }
        catch (ResourceDoesNotExistException e) {
            fail(e);
        }
    }

   /* @Test
    public void addAddressToClientException(){

    }*/
}
