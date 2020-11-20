package ru.bogatov.gatewayservice.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

@Component
public class AuthClient{
    RestTemplate restTemplate;
    private final String PATH = "http://localhost:8091/auth";

    public AuthClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }


    public UUID getIdFormToken(String token){


        try{
            ResponseEntity<UUID> response = restTemplate.postForEntity(new URI(PATH),token,UUID.class);
            return response.getBody();
        }catch (Exception e){
            return null;
        }
    }
}
