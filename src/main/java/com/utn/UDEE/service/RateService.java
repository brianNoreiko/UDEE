package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;

@Service
public class RateService {
    RateRepository rateRepository;

    @Autowired
    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }


    public Rate getRateById(Integer idRate) throws ResourceDoesNotExistException {
        return rateRepository.findById(idRate)
                .orElseThrow(()-> new ResourceDoesNotExistException("Rate doesn't exist"));
    }

    public Page<Rate> getAllRates(Pageable pageable) {
        return rateRepository.findAll(pageable);
    }

    public Rate addRate(Rate rate) throws ResourceAlreadyExistException {
        Boolean alreadyExist = rateRepository.existsById(rate.getId());
        if(alreadyExist == false){
            return rateRepository.save(rate);
        }else{
            throw new ResourceAlreadyExistException("Rate already exist");
        }
    }

    public void deleteRateById(Integer idRate) throws DeleteException, ResourceDoesNotExistException {
        Boolean alreadyExist = rateRepository.existsById(idRate);
        if(alreadyExist == false){
            throw new ResourceDoesNotExistException("Rate doesn't exist");
        }
        else {
            Rate rate = getRateById(idRate);
            if (rate.getAddressList().isEmpty()) {
                rateRepository.delete(rate);
            } else {
                throw new DeleteException("It cannot be deleted because another object depends on it");
            }
        }
    }

    public void updateRate(Integer idToUp, Rate newRate) throws ResourceDoesNotExistException, PrimaryKeyViolationException, ResourceAlreadyExistException {
        Rate toUpdate = getRateById(idToUp);
        if(toUpdate == null){
            throw new ResourceDoesNotExistException("Rate doesn't exist");
        }
        if(toUpdate.getId() != newRate.getId()){
            throw new PrimaryKeyViolationException("Primary key cannot be changed");
        }
        if(toUpdate.equals(newRate)){
            throw new ResourceAlreadyExistException("You are trying to update the same information! Rate already exist");
        }else{
            rateRepository.save(newRate);
        }

    }
}
