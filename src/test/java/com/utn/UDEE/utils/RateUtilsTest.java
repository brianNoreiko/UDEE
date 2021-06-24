package com.utn.UDEE.utils;

import com.utn.UDEE.models.Rate;
import com.utn.UDEE.models.dto.RateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RateUtilsTest {

    public static Rate aRate(){
        return Rate.builder().id(1).value(2.0).typeRate("").addressList(new ArrayList<>()).build();
    }
    public static RateDto aRateDto(){
        RateDto rateDto = new RateDto();
        rateDto.setId(1);
        rateDto.setValue(2.0);
        rateDto.setTypeRate("");
        return rateDto;
    }
    public static Page<Rate> aRatePage() {
        return new PageImpl<>(List.of(aRate()));
    }

    public static Page<Rate> aRateEmptyPage() {
        List<Rate> rateList = Collections.emptyList();
        return new PageImpl<>(rateList);
    }
}
