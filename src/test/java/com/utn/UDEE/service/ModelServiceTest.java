package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Brand;
import com.utn.UDEE.models.Model;
import com.utn.UDEE.repository.ModelRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static com.utn.UDEE.utils.ModelUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ModelServiceTest {
    private static ModelService modelService;
    private static ModelRepository modelRepository;

    @BeforeAll
    public static void setUp(){
        modelRepository=mock(ModelRepository.class);
        modelService = new ModelService(modelRepository);
    }
    @Test
    public void getAllModelsOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(modelRepository.findAll(pageable))
                .thenReturn(aModelPage());

        modelService.getAllModels(pageable);
        //Then
        Mockito.verify(modelRepository,Mockito.times(2)).findAll(pageable);
    }

    @Test
    public void getAllModelsNC(){ //NC== NO CONTENT
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        when(modelRepository.findAll(pageable))
                .thenReturn(aModelEmptyPage());

        Page<Model> modelPage = modelService.getAllModels(pageable);
        //Then
        Mockito.verify(modelRepository,Mockito.times(1)).findAll(pageable);
        assertEquals(aModelEmptyPage().getContent().size(),modelPage.getContent().size());
        assertEquals(aModelEmptyPage().getTotalPages(),modelPage.getTotalPages());
        assertEquals(aModelEmptyPage().getTotalElements(),modelPage.getTotalElements());
    }

    @Test
    public void getModelByIdOK() throws ResourceDoesNotExistException {
        //Given
        Integer id = anyInt();
        //When
        when(modelRepository.findById(id))
                .thenReturn(Optional.of(aModel()));

        Model model = modelService.getModelById(id);
        //Then
        Mockito.verify(modelRepository,times(1)).findById(id);
        assertEquals(aModel(),model);
    }

    @Test
    public void getModelByIdError() throws ResourceDoesNotExistException{
        //Given
        Integer id = anyInt();
        //When
        when(modelRepository.findById(1)).thenReturn(Optional.empty());
        //
        Assert.assertThrows(HttpClientErrorException.class,()->modelService.getModelById(id));
    }

    @Test
    public void addModelOK(){
        //Given
        Model amodel = aModel();
        //When
        try{
            when(modelRepository.existsById(anyInt())).thenReturn(false);
            when(modelRepository.save(amodel)).thenReturn(aModel());
            Model model = modelService.addNewModel(amodel);
            Assert.assertEquals(model,amodel);
        } catch (ResourceAlreadyExistException e) {
            addModelAlreadyExists();
        }
    }
    @Test
    private void addModelAlreadyExists() {
        //When
        when(modelRepository.existsById(anyInt())).thenReturn(true);
        //then
        Assert.assertThrows(ResourceAlreadyExistException.class,()->modelService.addNewModel(aModel()));
    }
}
