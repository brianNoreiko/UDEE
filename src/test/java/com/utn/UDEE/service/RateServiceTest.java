package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.RateRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static com.utn.UDEE.utils.BrandUtilsTest.aBrandEmptyPage;
import static com.utn.UDEE.utils.RateUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class RateServiceTest {
    private static RateRepository rateRepository;
    private static RateService rateService;

    @BeforeAll
    public static void setUp(){
        rateRepository= mock(RateRepository.class);
        rateService = new RateService(rateRepository);
    }

    @AfterEach
    public void after(){
        reset(rateRepository);
    }

    @Test
    public void getAllRatesOk(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(rateRepository.findAll(pageable))
                .thenReturn(aRatePage());

        Page<Rate> ratePage = rateService.getAllRates(pageable);
        //then
        Mockito.verify(rateRepository,Mockito.times(1)).findAll(pageable);
        assertEquals(aRatePage(), ratePage);
    }

    @Test
    public void getAllRatesNC(){ //NC== NO CONTENT
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(rateRepository.findAll(pageable))
                .thenReturn(aRateEmptyPage());

        Page<Rate> ratePage = rateService.getAllRates(pageable);
        //Then
        Mockito.verify(rateRepository,Mockito.times(1)).findAll(pageable);
        assertEquals(aBrandEmptyPage().getContent().size(),ratePage.getContent().size());
        assertEquals(aBrandEmptyPage().getTotalPages(),ratePage.getTotalPages());
        assertEquals(aBrandEmptyPage().getTotalElements(),ratePage.getTotalElements());
    }

    @Test
    public void getRateByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(rateRepository.findById(id))
                .thenReturn(Optional.of(aRate()));

        Rate rate = rateService.getRateById(id);
        //Then
        Mockito.verify(rateRepository,times(1)).findById(id);
        assertEquals(aRate(),rate);
    }

    @Test
    public void getRateByIdError() throws ResourceDoesNotExistException{
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(rateRepository.findById(1)).thenReturn(Optional.empty());
        //Then
        Assert.assertThrows(ResourceDoesNotExistException.class,()->rateService.getRateById(id));
        verify(rateRepository,times(1)).findById(id);
    }

    @Test
    public void addRateOK(){
        //Given
        Rate aRate = aRate();
        //When
        try{
            when(rateRepository.existsById(aRate.getId())).thenReturn(false);
            when(rateRepository.save(aRate)).thenReturn(aRate);

            Rate rate = rateService.addRate(aRate);

            Assert.assertEquals(aRate(),rate);
            verify(rateRepository,times(1)).existsById(aRate.getId());
            verify(rateRepository,times(1)).save(aRate);
        } catch (ResourceAlreadyExistException e){
            addRateAlreadyExist();
        }
    }

    @Test
    public void addRateAlreadyExist(){
        //Given
        Rate aRate = aRate();
        //When
        when(rateRepository.existsById(aRate.getId())).thenReturn(true);
        //Then
        Assert.assertThrows(ResourceAlreadyExistException.class,()->rateService.addRate(aRate()));
        verify(rateRepository,times(1)).existsById(aRate.getId());
        verify(rateRepository,times(0)).save(aRate);
    }

    @Test
    public void deleteRateByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idRate = anyInt();
        //When
        try {
            when(rateRepository.existsById(idRate)).thenReturn(true);
            when(rateService.getRateById(idRate)).thenReturn(aRate());
            when(aRate().getAddressList().isEmpty()).thenReturn(true);
            doNothing().when(rateRepository).delete(aRate());

            rateService.deleteRateById(idRate);
            //Then
            verify(rateRepository,times(1)).existsById(idRate);
            verify(rateService,times(1)).getRateById(idRate);
            verify(aRate().getAddressList(),times(1)).isEmpty();
            verify(rateRepository,times(1)).delete(aRate());
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteRateDenied();
        }
    }

    @Test
    public void deleteRateByIdNotExist() throws ResourceDoesNotExistException, DeleteException{
        //Given
        Integer idRate = anyInt();
        //When
        when(rateRepository.existsById(idRate)).thenReturn(false);
        //Then
        assertThrows(ResourceDoesNotExistException.class, () -> rateService.deleteRateById(idRate));
        verify(rateRepository,times(1)).existsById(idRate);
    }

    @Test
    public void deleteRateByIdException() throws ResourceDoesNotExistException, DeleteException{
        //Given
        Integer idRate = anyInt();
        //When
        try {
            when(rateRepository.existsById(idRate)).thenReturn(true);
            when(rateService.getRateById(idRate)).thenReturn(aRate());
            when(aRate().getAddressList().isEmpty()).thenReturn(false);

            rateService.deleteRateById(idRate);
            //Then
            assertThrows(DeleteException.class,() -> rateService.deleteRateById(idRate));
            verify(rateRepository,times(1)).existsById(idRate);
            verify(rateService,times(1)).getRateById(idRate);
            verify(aRate().getAddressList(),times(1)).isEmpty();
            verify(rateRepository,times(0)).delete(aRate());
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteRateDenied();
        }
    }

    @Test
    public void updateRateOK() throws ResourceDoesNotExistException, PrimaryKeyViolationException, ResourceAlreadyExistException {
        //Given
        Integer idToUp = anyInt();
        Rate aRate = aRate();
        //When
        try {
            when(rateService.getRateById(idToUp)).thenReturn(aRate());
            when(aRate().equals(aRate)).thenReturn(false);
            when(aRate() == null).thenReturn(false);
            when(aRate().getId() != aRate.getId()).thenReturn(false);
            doNothing().when(rateRepository).save(aRate);
            //Then
            verify(rateService, times(1)).getRateById(idToUp);
            verify(aRate(), times(1)).equals(aRate);
            verify(rateRepository, times(1)).save(aRate);
        }catch (ResourceDoesNotExistException e){
            updateRateDenied();
        }
    }

    @Test
    public void updateRateNotExist() throws ResourceDoesNotExistException, PrimaryKeyViolationException, ResourceAlreadyExistException {
        //Given
        Integer idToUp = anyInt();
        Rate aRate = aRate();
        //When
        try {
            when(rateService.getRateById(idToUp)).thenReturn(null);
            when(aRate() == null).thenReturn(true);
            //Then
            verify(rateService, times(1)).getRateById(idToUp);
            verify(aRate(), times(0)).equals(aRate);
            verify(aRate(),times(0)).getId();
            verify(aRate,times(0)).getId();
            verify(rateRepository, times(0)).save(aRate);
            assertThrows(ResourceDoesNotExistException.class,() -> rateService.updateRate(idToUp,aRate));
        }catch (ResourceDoesNotExistException e){
            updateRateDenied();
        }
    }

    @Test
    public void updateRatePKviolation() throws ResourceDoesNotExistException, PrimaryKeyViolationException, ResourceAlreadyExistException {
        //Given
        Integer idToUp = anyInt();
        Rate aRate = aRate();
        //When
        try {
            when(rateService.getRateById(idToUp)).thenReturn(aRate());
            when(aRate() == null).thenReturn(false);
            when(aRate().getId() != aRate.getId()).thenReturn(true);
            //Then
            verify(rateService, times(1)).getRateById(idToUp);
            verify(aRate(),times(1)).getId();
            verify(aRate,times(1)).getId();
            verify(aRate(), times(0)).equals(aRate);
            verify(rateRepository, times(0)).save(aRate);
            assertThrows(PrimaryKeyViolationException.class,() -> rateService.updateRate(idToUp,aRate));
        }catch (ResourceDoesNotExistException e){
            updateRateDenied();
        }
    }

    @Test
    public void updateRateAlreadyExist() throws ResourceDoesNotExistException, PrimaryKeyViolationException, ResourceAlreadyExistException {
        //Given
        Integer idToUp = anyInt();
        Rate aRate = aRate();
        //When
        try {
            when(rateService.getRateById(idToUp)).thenReturn(aRate());
            when(aRate() == null).thenReturn(false);
            when(aRate().getId() != aRate.getId()).thenReturn(false);
            when(aRate().equals(aRate)).thenReturn(true);
            //Then
            verify(rateService, times(1)).getRateById(idToUp);
            verify(aRate(), times(1)).equals(aRate);
            verify(aRate(),times(1)).getId();
            verify(aRate,times(1)).getId();
            verify(rateRepository, times(0)).save(aRate);
            assertThrows(ResourceAlreadyExistException.class,() -> rateService.updateRate(idToUp,aRate));
        }catch (ResourceDoesNotExistException e){
            updateRateDenied();
        }
    }

    @SneakyThrows
    @Test
    public void deleteRateDenied(){
        Integer idRate = anyInt();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->rateService.deleteRateById(idRate));
    }

    @SneakyThrows
    @Test
    public void updateRateDenied(){
        Integer idRate = anyInt();
        Rate aRate = aRate();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->rateService.updateRate(idRate,aRate));
    }

}
