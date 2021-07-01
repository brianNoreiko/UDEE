package com.utn.UDEE.service;

import com.utn.UDEE.exception.AccessNotAllowedException;
import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Invoice;
import com.utn.UDEE.models.User;
import com.utn.UDEE.models.UserType;
import com.utn.UDEE.repository.InvoiceRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.InvoiceUtilsTest.*;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeSince;
import static com.utn.UDEE.utils.LocalDateTimeUtilsTest.aLocalDateTimeUntil;
import static com.utn.UDEE.utils.UserUtilsTest.aUser;
import static com.utn.UDEE.utils.UserUtilsTest.aUserDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class InvoiceServiceTest {
    private static InvoiceRepository invoiceRepository;
    private static UserService userService;
    private static AddressService addressService;
    private static MeasurementService measurementService;
    private static MeterService meterService;
    private static InvoiceService invoiceService;

    @BeforeAll
    public static void setUp(){
        invoiceRepository = mock(InvoiceRepository.class);
        userService = mock(UserService.class);
        addressService = mock(AddressService.class);
        measurementService = mock(MeasurementService.class);
        meterService = mock(MeterService.class);
        invoiceService = new InvoiceService(invoiceRepository, userService, addressService, measurementService, meterService);
    }

    @AfterEach
    public void after(){
        reset(invoiceRepository);
        reset(meterService);
        reset(measurementService);
        reset(userService);
        reset(addressService);
    }
    /*
    @Test
    public void getInvoiceBetweenDateByUserOK(){
        //Given
        Integer idUser = anyInt();
        Integer page = 1;
        Integer size = 1;
        LocalDateTime since = aLocalDateTimeSince();
        LocalDateTime until = aLocalDateTimeUntil();
        Pageable pageable = PageRequest.of(page,size);
        //When
        try {
            when(userService.getUserById(idUser)).thenReturn(aUser());
            when(invoiceRepository.findAllByUserAndDateBetween(aUser(), since, until, pageable)).thenReturn(aInvoicePage());

            Page<Invoice> invoicePage = invoiceService.getInvoiceBetweenDateByUser(idUser, since, until, pageable);
            //Then
            assertEquals(aInvoicePage(), invoicePage);
            verify(userService,times(1)).getUserById(idUser);
            verify(invoiceRepository, times(1)).findAllByUserAndDateBetween(aUser(),since,until,pageable);
        }catch (ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void getInvoiceBetweenDateByUserNC() throws ResourceDoesNotExistException {    //NC == No Content
        //Given
        Integer idUser = anyInt();
        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pageable pageable = PageRequest.of(1,1);
        //When
        try {
            when(userService.getUserById(idUser)).thenReturn(aUser());
            when(invoiceRepository.findAllByUserAndDateBetween(aUser(), since, until, pageable)).thenReturn(aInvoiceEmptyPage());
            Page<Invoice> invoicePage = invoiceService.getInvoiceBetweenDateByUser(idUser, since, until, pageable);

            //Then
            assertEquals(aInvoiceEmptyPage(),invoicePage);
            verify(userService,times(1)).getUserById(idUser);
            verify(invoiceRepository,times(1)).findAllByUserAndDateBetween(aUser(),since,until,pageable);
        }catch (ResourceDoesNotExistException e){
            fail(e);
        }
    }

    @Test
    public void getInvoiceBetweenDateByUserFail() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = anyInt();
        LocalDateTime since = LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime until = LocalDateTime.parse("2020-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(null);
        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> invoiceService.getInvoiceBetweenDateByUser(idUser,since,until,pageable));
        verify(userService,times(1)).getUserById(idUser);
        verify(invoiceRepository, times(0)).findAllByUserAndDateBetween(aUser(),since,until,pageable);
    }*/
    @Test
    public void getUnpaidByUserOK() throws ResourceDoesNotExistException {
        //Given
        User queryUser = aUser();
        User user = aUser();
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        try {
            when(userService.getUserById(queryUser.getId())).thenReturn(aUser());
            when(userService.getUserById(user.getId())).thenReturn(aUser());
            when(invoiceRepository.findAllByUserAndPayed(user,false,pageable)).thenReturn(aInvoicePage());

            Page<Invoice> invoicePage = invoiceService.getUnpaidByUser(queryUser.getId(),user.getId(),pageable);

            assertEquals(aInvoicePage(), invoicePage);
            verify(invoiceRepository, times(1)).findAllByUserAndPayed(user,false,pageable);
            verify(userService, times(2)).getUserById(aUser().getId());
        }
        catch (AccessNotAllowedException e) {
            fail(e);
        }
    }

    @Test
    public void getUnpaidByUserButNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer page = 1;
        Integer size = 1;
        User queryUser = aUser();
        User user = aUser();
        queryUser.setUserType(UserType.EMPLOYEE);
        user.setUserType(UserType.EMPLOYEE);

        Pageable pageable = PageRequest.of(page,size);
        try {
            when(userService.getUserById(queryUser.getId())).thenReturn(queryUser);
            when(userService.getUserById(user.getId())).thenReturn(user);
            when(invoiceRepository.findAllByUserAndPayed(user,false,pageable)).thenReturn(aInvoicePage());

            assertThrows(ResourceDoesNotExistException.class, ()-> {
                Page<Invoice> invoicePage = invoiceService.getUnpaidByUser(queryUser.getId(),user.getId(),pageable);
            });

            verify(userService, times(2)).getUserById(aUser().getId());
        }
        catch (ResourceDoesNotExistException e) {
            fail(e);
        }
    }

    @Test
    public void getUnpaidByUserAccessNotAllowed() {
        Integer page = 1;
        Integer size = 1;
        User queryUser = aUser();
        User user = aUser();
        user.setId(2);

        Pageable pageable = PageRequest.of(page,size);
        try {
            when(userService.getUserById(queryUser.getId())).thenReturn(queryUser);
            when(userService.getUserById(user.getId())).thenReturn(user);
            when(invoiceRepository.findAllByUserAndPayed(user,false,pageable)).thenReturn(aInvoicePage());

            assertThrows(AccessNotAllowedException.class, ()-> {
                Page<Invoice> invoicePage = invoiceService.getUnpaidByUser(queryUser.getId(),user.getId(),pageable);
            });

            verify(userService, times(1)).getUserById(queryUser.getId());
            verify(userService, times(1)).getUserById(user.getId());
        }
        catch (ResourceDoesNotExistException e) {
            fail(e);
        }
    }

    /*@Test
    public void getUnpaidByUserNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idUser = 1;
        Integer idQueryUser = 1;
        Integer page = 1;
        Integer size = 1;
        Pageable pageable = PageRequest.of(page,size);
        Authentication authentication = mock(Authentication.class);
        //When
        try {
            when(authentication.getPrincipal()).thenReturn(aUserDto());
            when(userService.getUserById(idUser)).thenReturn(aUser());
            when(invoiceRepository.findAllByUserAndPayed(aUser(), false, pageable)).thenReturn(aInvoiceEmptyPage());
            Page<Invoice> invoicePage = invoiceService.getUnpaidByUser(idQueryUser,idUser,pageable);
            //Then
            assertEquals(0,invoicePage.getContent().size());
            verify(userService,times(1)).getUserById(idUser);
            verify(invoiceRepository,times(1)).findAllByUserAndPayed(aUser(),false, pageable);
        }catch (ResourceDoesNotExistException | AccessNotAllowedException e){
            fail(e);
        }
    }*/

    @Test
    public void addNewInvoiceOK() throws ResourceAlreadyExistException {
        //Given
        Invoice invoice = aInvoice();
        //When
        when(invoiceRepository.existsById(invoice.getId())).thenReturn(false);
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        Invoice savedInvoice = invoiceService.addNewInvoice(invoice);
        //Then
        assertEquals(aInvoice(),savedInvoice);
        verify(invoiceRepository,times(1)).existsById(invoice.getId());
        verify(invoiceRepository,times(1)).save(invoice);
    }

    @Test
    public void addNewInvoiceAlreadyExist() throws ResourceAlreadyExistException {
        //Given
        Invoice invoice = aInvoice();
        //When
        when(invoiceRepository.existsById(invoice.getId())).thenReturn(true);
        //Then
        assertThrows(ResourceAlreadyExistException.class,() -> invoiceService.addNewInvoice(invoice));
        verify(invoiceRepository,times(1)).existsById(invoice.getId());
        verify(invoiceRepository,times(0)).save(invoice);
    }

   @Test
    public void deleteInvoiceByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idInvoice = anyInt();
        //When
        try {
            when(invoiceService.getInvoiceById(idInvoice)).thenReturn(aInvoice());
            when(aInvoice().getUser()).thenReturn(null);
            doNothing().when(invoiceRepository).deleteById(idInvoice);

            invoiceService.deleteInvoiceById(idInvoice);
        //Then
        verify(invoiceService,times(1)).getInvoiceById(idInvoice);
        verify(invoiceRepository,times(1)).deleteById(idInvoice);
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteInvoiceDenied();
        }
    }

    @Test
    public void deleteInvoiceByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idInvoice = anyInt();
        //When
        try {
            when(invoiceService.getInvoiceById(idInvoice)).thenReturn(null);
            //Then
            assertThrows(ResourceDoesNotExistException.class,() -> invoiceService.deleteInvoiceById(idInvoice));
            verify(invoiceService,times(1)).getInvoiceById(idInvoice);
            verify(invoiceRepository,times(0)).deleteById(idInvoice);
        }catch (ResourceDoesNotExistException e){
            deleteInvoiceDenied();
        }
    }


   @SneakyThrows
   @Test
   public void deleteInvoiceDenied(){
       Integer idInvoice = anyInt();
       Assert.assertThrows(ResourceDoesNotExistException.class, ()->invoiceService.deleteInvoiceById(idInvoice));
   }

    @Test
    public void getInvoiceByIdOK() throws ResourceDoesNotExistException{
        //Given
        Integer idInvoice = anyInt();
        //When
        when(invoiceRepository.findById(idInvoice)).thenReturn(Optional.of(aInvoice()));
        Invoice invoice = invoiceService.getInvoiceById(idInvoice);
        //Then
        assertEquals(aInvoice(),invoice);
        verify(invoiceRepository,times(1)).findById(idInvoice);
    }

    @Test
    public void getInvoiceByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idInvoice = anyInt();
        //When
        when(invoiceRepository.findById(idInvoice)).thenReturn(Optional.empty());
        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> invoiceService.getInvoiceById(idInvoice));
        verify(invoiceRepository, times(1)).findById(idInvoice);
    }

    @Test
    public void getAllUnpaidByAddressOK() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
        when(invoiceRepository.findAllByAddressAndPayed(aAddress(),false,pageable)).thenReturn(aInvoicePage());
        Page<Invoice> invoicePage = invoiceService.getAllUnpaidByAddress(idAddress, pageable);
        //Then
        assertEquals(aInvoicePage(),invoicePage);
        verify(addressService,times(1)).getAddressById(idAddress);
        verify(invoiceRepository,times(1)).findAllByAddressAndPayed(aAddress(),false,pageable);
    }

    @Test
    public void getAllUnpaidByAddressNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idAddress = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(addressService.getAddressById(idAddress)).thenReturn(null);

        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> invoiceService.getAllUnpaidByAddress(idAddress,pageable));
        verify(addressService,times(1)).getAddressById(idAddress);
        verify(invoiceRepository,times(0)).findAllByAddressAndPayed(aAddress(),false,pageable);
    }

    @Test
    public void getAllUnpaidByAddressNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idAddress = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(addressService.getAddressById(idAddress)).thenReturn(aAddress());
        when(invoiceRepository.findAllByAddressAndPayed(aAddress(),false,pageable)).thenReturn(aInvoiceEmptyPage());
        Page<Invoice> invoicePage = invoiceService.getAllUnpaidByAddress(idAddress, pageable);
        //Then
        assertEquals(aInvoiceEmptyPage(), invoicePage);
        verify(addressService,times(1)).getAddressById(idAddress);
        verify(invoiceRepository,times(1)).findAllByAddressAndPayed(aAddress(),false,pageable);
    }

    @Test
    public void getAllInvoicesOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(invoiceRepository.findAll(pageable)).thenReturn(aInvoicePage());
        Page<Invoice> invoicePage = invoiceService.getAllInvoices(pageable);
        //Then
        assertEquals(aInvoicePage(),invoicePage);
        verify(invoiceRepository,times(1)).findAll(pageable);
    }

    @Test
    public void getAllInvoicesNC(){ //NC == No Content
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(invoiceRepository.findAll(pageable)).thenReturn(aInvoiceEmptyPage());
        Page<Invoice> invoicePage = invoiceService.getAllInvoices(pageable);
        //Then
        assertEquals(aInvoiceEmptyPage(),invoicePage);
        verify(invoiceRepository,times(1)).findAll(pageable);
    }

    @Test
    public void getAllInvoicesByUserOK() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(invoiceRepository.findAllByUser(aUser(),pageable)).thenReturn(aInvoicePage());
        Page<Invoice> invoicePage = invoiceService.getAllInvoicesByUser(idUser, pageable);
        //Then
        assertEquals(aInvoicePage(), invoicePage);
        verify(userService,times(1)).getUserById(idUser);
        verify(invoiceRepository,times(1)).findAllByUser(aUser(),pageable);
    }

    @Test
    public void getAllInvoicesByUserNotExist() throws ResourceDoesNotExistException {
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(null);
        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> invoiceService.getAllInvoicesByUser(idUser,pageable));
        verify(userService,times(1)).getUserById(idUser);
        verify(invoiceRepository,times(0)).findAllByUser(aUser(),pageable);
    }

    @Test
    public void getAllInvoicesByUserNC() throws ResourceDoesNotExistException { //NC == No Content
        //Given
        Integer idUser = anyInt();
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(userService.getUserById(idUser)).thenReturn(aUser());
        when(invoiceRepository.findAllByUser(aUser(),pageable)).thenReturn(aInvoiceEmptyPage());
        Page<Invoice> invoicePage = invoiceService.getAllInvoicesByUser(idUser, pageable);
        //Then
        assertEquals(aInvoiceEmptyPage(), invoicePage);
        verify(userService,times(1)).getUserById(idUser);
        verify(invoiceRepository,times(1)).findAllByUser(aUser(),pageable);
    }


}
