package com.utn.UDEE.service;

import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.RateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class RateService {
    RateRepository rateRepository;


    public Rate getRateById(Integer idRate) {
        return rateRepository.findById(idRate).orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }
}
