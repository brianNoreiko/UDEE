package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Brand;
import com.utn.UDEE.repository.BrandRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.utn.UDEE.utils.BrandUtilsTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BrandServiceTest {
    private static BrandRepository brandRepository;
    private static BrandService brandService;

    @BeforeAll
    public static void setUp(){
        brandRepository = mock(BrandRepository.class);
        brandService = new BrandService(brandRepository);
    }

    @Test
    public void getAllBrandsOK(){
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(aBrandPage());

        brandService.getAllBrands(pageable);
        //
        Mockito.verify(brandRepository,Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void getAllBrandsNC(){ //NC== NO CONTENT
        //Given
        Pageable pageable = PageRequest.of(1,1);
        //When
        Mockito.when(brandRepository.findAll(pageable))
                .thenReturn(aBrandEmptyPage());

        Page<Brand> brandPage = brandService.getAllBrands(pageable);
        //Then
        Mockito.verify(brandRepository,Mockito.times(1)).findAll(pageable);
        assertEquals(aBrandEmptyPage().getContent().size(),brandPage.getContent().size());
        assertEquals(aBrandEmptyPage().getTotalPages(),brandPage.getTotalPages());
        assertEquals(aBrandEmptyPage().getTotalElements(),brandPage.getTotalElements());
    }

    @Test
    public void getBrandByIdOK() throws ResourceDoesNotExistException{
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(brandRepository.findById(id))
        .thenReturn(Optional.of(aBrand()));

        Brand brand = brandService.getBrandById(id);
        //Then
        Mockito.verify(brandRepository,times(1)).findById(id);
        assertEquals(aBrand(),brand);
    }

    @Test
    public void getBrandByIdError() throws ResourceDoesNotExistException{
        //Given
        Integer id = anyInt();
        //When
        Mockito.when(brandRepository.findById(1)).thenReturn(Optional.empty());
        //
        Assert.assertThrows(ResourceDoesNotExistException.class,()->brandService.getBrandById(id));
    }

    @Test
    public void addBrandOK(){
        //Given
        Brand abrand =aBrand();
        //When
        try{
            when(brandRepository.existsById(anyInt())).thenReturn(false);
            when(brandRepository.save(abrand)).thenReturn(aBrand());

            Brand brand = brandService.addBrand(abrand);

            Assert.assertEquals(brand,abrand);

        } catch (ResourceAlreadyExistException  e) {
           addBrandAlreadyExists();
        }
    }

    @Test
    public void addBrandAlreadyExists(){
        //When
        when(brandRepository.existsById(anyInt())).thenReturn(true);
        //Then
        Assert.assertThrows(ResourceAlreadyExistException.class,()->brandService.addBrand(aBrand()));
    }
}
