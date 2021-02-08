package com.github.dbranco.jaguar.service;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dbranco.jaguar.model.JdkClassification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The service to list the JDKs remotely available.
 */
@Service
public class JdkListRemoteService implements JdkListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdkListRemoteService.class);

    @Value("${jaguar.list.url}")
    private String listUrl;

    public void list() {
        WebClient client = WebClient.builder().baseUrl(listUrl).build();

        String bodyToMono = client.get().retrieve().bodyToMono(String.class).block();

        ObjectMapper objectMapper = new ObjectMapper();

        List<JdkClassification> allJdksAvailable = Collections.emptyList();
        try {
            allJdksAvailable = objectMapper.readValue(bodyToMono, new TypeReference<>() {
                @Override
                public Type getType() {
                    return new ParameterizedTypeReference<List<JdkClassification>>() {
                    }.getType();
                }
            });
        } catch (JsonProcessingException theCause) {
            LOGGER.error("Error while trying to de-serialised the remote list", theCause);
        }
        
        System.out.println(allJdksAvailable);
    }
    
}
