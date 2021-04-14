package com.utn.UDEE.service;

import com.utn.UDEE.models.Client;
import com.utn.UDEE.repository.clientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class clientService {
    @Autowired
    private clientRepository clientRepository;

    public clientService(clientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll(){ return clientRepository.findAll();
    }

    public Client getById(Long id){
        return clientRepository.findById(id).
                orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public void add(Client client){
        clientRepository.save(client);
    }

    public void deleteClient(Long id){
        clientRepository.deleteById(id);
    }

    /*public void updateClient(Client client){
        if (clientRepository.existsById(client.getIdClient())){

        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }*/
}
