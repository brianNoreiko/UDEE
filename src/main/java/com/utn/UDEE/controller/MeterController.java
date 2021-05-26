package com.utn.UDEE.controller;

import com.utn.UDEE.models.Meter;
import com.utn.UDEE.service.MeterService;
import com.utn.UDEE.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Meter")
public class MeterController {

    MeterService meterService;
    PersonService personService;

    @Autowired
    public MeterController(MeterService meterService, PersonService personService) {
        this.meterService = meterService;
        this.personService = personService;
    }

    @GetMapping
    public List<Meter> getAllMeters(){return meterService.getAllMeters();}

    @PostMapping
    public void addMeter(@RequestBody Meter meter){meterService.addMeter(meter);}

}
