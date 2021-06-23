package com.utn.UDEE.service;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.models.Model;
import com.utn.UDEE.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ModelService {

    private ModelRepository modelRepository;


    @Autowired
    public ModelService(ModelRepository modelRepository){
        this.modelRepository = modelRepository;
    }

    public Model getModelById(Integer id) {
        return modelRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public Model addNewModel(Model model) throws ResourceAlreadyExistException {
        Model alreadyExist = getModelById(model.getId());
        if(alreadyExist == null){
            return modelRepository.save(model);
        }else {
            throw new ResourceAlreadyExistException("Model Already Exist");
        }
    }
}

