package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.models.Meter;
import com.utn.UDEE.service.MeterService;
import com.utn.UDEE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/backoffice/meter")
public class MeterBackOfficeController {

    MeterService meterService;

    @Autowired
    public MeterBackOfficeController(MeterService meterService) {
        this.meterService = meterService;
    }


}
