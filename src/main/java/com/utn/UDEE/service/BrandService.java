package com.utn.UDEE.service;

import com.utn.UDEE.exception.alreadyExist.BrandAlreadyExist;
import com.utn.UDEE.models.Brand;
import com.utn.UDEE.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;

@Service
public class BrandService {

    @Autowired
    BrandRepository brandRepository;

    public Page<Brand> getAllBrands(Pageable pageable){
        return brandRepository.findAll(pageable);
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Brand addBrand(Brand brand) throws BrandAlreadyExist {
        Brand brandExist = getBrandById(brand.getId());
        if(isNull(brandExist)){
            return brandRepository.save(brand);
        }else{
            throw new BrandAlreadyExist("Brand already exists");
        }
    }

}
