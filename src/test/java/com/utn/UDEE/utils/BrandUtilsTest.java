package com.utn.UDEE.utils;

import com.utn.UDEE.models.Brand;
import com.utn.UDEE.models.dto.BrandDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BrandUtilsTest {

    public  static Brand aBrand(){
        return Brand.builder().id(1).name("CASSIO").modelList(new ArrayList<>()).build();
    }

    public static BrandDto aBrandDto(){
        BrandDto brandDto = new BrandDto();
        brandDto.setId(1);
        brandDto.setName("Lux");
        return brandDto;
    }

    public static Page<Brand> aBrandPage(){return new PageImpl<>(List.of(aBrand()));}

    public static Page<Brand> aBrandEmptyPage(){
        List<Brand> brandList = Collections.emptyList();
        return new PageImpl<>(brandList);
    }
}

