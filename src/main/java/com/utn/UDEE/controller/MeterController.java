package com.utn.UDEE.controller;

import com.utn.UDEE.models.Meter;
import com.utn.UDEE.service.MeterService;
import com.utn.UDEE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Meter")
public class MeterController {

    MeterService meterService;

    @Autowired
    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }


}
