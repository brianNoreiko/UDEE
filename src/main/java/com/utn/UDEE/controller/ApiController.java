package com.utn.UDEE.controller;

import com.utn.UDEE.api.ApiCallService;
import com.utn.UDEE.api.MeasurementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiCallService apiCallService;
    /*@GetMapping
    public MeasurementResponse getMeasurement(){
        return apiCallService.getMeasurement(); //Falta resolver esto
    }*/
}
