package com.utn.UDEE.utils;


import com.utn.UDEE.models.Model;
import com.utn.UDEE.models.dto.ModelDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.utn.UDEE.utils.BrandUtilsTest.aBrand;
import static com.utn.UDEE.utils.BrandUtilsTest.aBrandDto;

public class ModelUtilsTest {
    public static Model aModel() {
        return Model.builder().id(1).name("Model").Brand(aBrand()).meterList(new ArrayList<>()).build();
    }

    public static ModelDto aModelDto(){
        ModelDto modelDto = new ModelDto();
        modelDto.setId(1);
        modelDto.setName("energy");
        modelDto.setBrandDto(aBrandDto());
        return modelDto;
    }
    public static Page<Model> aModelPage(){return new PageImpl<>(List.of(aModel()));}

    public static Page<Model> aModelEmptyPage(){
        List<Model> modelList = Collections.emptyList();
        return new PageImpl<>(modelList);
    }
}
