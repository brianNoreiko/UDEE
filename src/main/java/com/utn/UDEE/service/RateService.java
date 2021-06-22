package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.alreadyExist.RateAlreadyExist;
import com.utn.UDEE.exception.doesNotExist.RateDoesNotExist;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.RateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class RateService {
    RateRepository rateRepository;


    public Rate getRateById(Integer idRate) {
        return rateRepository.findById(idRate).orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Page<Rate> getAllRates(Pageable pageable) {
        return rateRepository.findAll(pageable);
    }

    public Rate addRate(Rate rate) throws RateAlreadyExist {
        Rate alreadyExist = getRateById(rate.getId());
        if(alreadyExist == null){
            return rateRepository.save(rate);
        }else{
            throw new RateAlreadyExist("Rate already exist");
        }
    }

    public void deleteRateById(Integer idRate) throws DeleteException, RateDoesNotExist {
        Rate rate = getRateById(idRate);
        if(rate == null){
            throw new RateDoesNotExist("Rate doesn't exist");
        }
        if(rate.getAddressList().isEmpty()){
            rateRepository.delete(rate);
        }else{
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }

    public void updateRate(Integer idToUp, Rate newRate) throws RateDoesNotExist, PrimaryKeyViolationException, RateAlreadyExist {
        Rate toUpdate = getRateById(idToUp);
        if(toUpdate == null){
            throw new RateDoesNotExist("Rate doesn't exist");
        }
        if(toUpdate.getId() != newRate.getId()){
            throw new PrimaryKeyViolationException("Primary key cannot be changed");
        }
        if(toUpdate.equals(newRate)){
            throw new RateAlreadyExist("You are trying to update the same information! Rate already exist");
        }else{
            rateRepository.save(newRate);
        }

    }
}
