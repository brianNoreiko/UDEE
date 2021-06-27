package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.RateRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
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

    @Test
    public void getAllRatesOk(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(rateRepository.findAll(pageable))
                .thenReturn(aRatePage());

        rateService.getAllRates(pageable);
        //then
        Mockito.verify(rateRepository,Mockito.times(2)).findAll(pageable);
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
    public void getRateByIdIk() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(rateRepository.findById(id))
                .thenReturn(Optional.of(aRate()));

        Rate rate = rateService.getRateById(id);
        //Then
        Mockito.verify(rateRepository,times(2)).findById(id);
        assertEquals(aRate(),rate);
    }

    @Test
    public void getRateByIdError() throws ResourceDoesNotExistException{
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(rateRepository.findById(1)).thenReturn(Optional.empty());
        //Then
        Assert.assertThrows(HttpClientErrorException.class,()->rateService.getRateById(id));
    }

    @Test
    public void addRateOK(){
        //Given
        Rate arate = aRate();
        //When
        try{
            when(rateRepository.existsById(anyInt())).thenReturn(false);
            when(rateRepository.save(arate)).thenReturn(aRate());

            Rate rate = rateService.addRate(arate);

            Assert.assertEquals(rate,arate);
        } catch (ResourceAlreadyExistException e){
            addRateAlreadyExist();
        }
    }

    @Test
    public void addRateAlreadyExist(){
        //When
        when(rateRepository.existsById(anyInt())).thenReturn(true);
        //Then
        Assert.assertThrows(ResourceAlreadyExistException.class,()->rateService.addRate(aRate()));
    }

    @Test
    public void deleteRateByIdOK() throws ResourceDoesNotExistException, DeleteException {
        //Given
        Integer idRate = anyInt();
        //When
        try {
            when(rateService.getRateById(idRate)).thenReturn(aRate());
            when(aRate().getAddressList()).thenReturn(null);
            doNothing().when(rateRepository).deleteById(idRate);

            rateService.deleteRateById(idRate);
            //Then
            verify(rateService,times(1)).getRateById(idRate);
            verify(rateRepository,times(1)).deleteById(idRate);
        }catch (ResourceDoesNotExistException | DeleteException e){
            deleteRateDenied();
        }
    }

    @Test
    public void deleteRateByIdNotExist() throws ResourceDoesNotExistException{
        //Given
        Integer idRate = anyInt();
        //When
        when(rateService.getRateById(idRate)).thenReturn(null);
        //Then
        assertThrows(ResourceDoesNotExistException.class,() -> rateService.deleteRateById(idRate));
        verify(rateService,times(1)).getRateById(idRate);
        verify(rateRepository,times(0)).deleteById(idRate);
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
        Rate rate = aRate();
        Assert.assertThrows(ResourceDoesNotExistException.class, ()->rateService.updateRate(1,rate));
    }
}
