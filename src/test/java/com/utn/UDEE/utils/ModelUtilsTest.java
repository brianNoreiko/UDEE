package com.utn.UDEE.utils;

import com.utn.UDEE.models.Model;

public class ModelUtilsTest {
    public static Model aModel() {
        return Model.builder().id(1).name("Model").build();
    }
}
