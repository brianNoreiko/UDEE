package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.models.Brand;
import com.utn.UDEE.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class BrandService {

    BrandRepository brandRepository;
    @Autowired
    public  BrandService(BrandRepository brandRepository){
        this.brandRepository = brandRepository;
    }

    public Page<Brand> getAllBrands(Pageable pageable){
        return brandRepository.findAll(pageable);
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Brand addBrand(Brand brand) throws ResourceAlreadyExistException {
        boolean brandExist = brandRepository.existsById(brand.getId());
        if(brandExist==false){
            return brandRepository.save(brand);
        }else{
            throw new ResourceAlreadyExistException("Brand already exists");
        }
    }

}
