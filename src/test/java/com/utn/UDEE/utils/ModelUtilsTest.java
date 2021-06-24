package com.utn.UDEE.utils;


import com.utn.UDEE.models.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.utn.UDEE.utils.BrandUtilsTest.aBrand;

public class ModelUtilsTest {
    public static Model aModel() {
        return Model.builder().id(1).name("Model").Brand(aBrand()).meterList(new ArrayList<>()).build();
    }
    public static Page<Model> aModelPage(){return new PageImpl<>(List.of(aModel()));}

    public static Page<Model> aModelEmptyPage(){
        List<Model> modelList = Collections.emptyList();
        return new PageImpl<>(modelList);
    }
}
