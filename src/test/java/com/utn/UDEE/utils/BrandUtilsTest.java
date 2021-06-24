package com.utn.UDEE.utils;

import com.utn.UDEE.models.Brand;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.dto.MeterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.h2.util.MathUtils.randomInt;
import static org.mockito.ArgumentMatchers.anyInt;


public class BrandUtilsTest {

    public  static Brand aBrand(){
        return Brand.builder().id(1).name("CASSIO").modelList(new ArrayList<>()).build();
    }
    public static Page<Brand> aBrandPage(){return new PageImpl<>(List.of(aBrand()));}

    public static Page<Brand> aBrandEmptyPage(){
        List<Brand> brandList = Collections.emptyList();
        return new PageImpl<>(brandList);
    }
}

