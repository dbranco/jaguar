package com.github.dbranco.jaguar.service;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dbranco.jaguar.config.JaguarConfigurationProperties;
import com.github.dbranco.jaguar.config.JaguarConfigurationProperties.ListProperties;
import com.github.dbranco.jaguar.model.JdkClassification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The service to list the JDKs remotely available.
 */
@Service
public class JdkListRemoteService implements JdkListService<JdkClassification> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdkListRemoteService.class);

    private ListProperties listProperties;
    
    @Autowired
    public JdkListRemoteService(JaguarConfigurationProperties theJaguarConfigurations) {
        listProperties = theJaguarConfigurations.getList();
    }

    public List<JdkClassification> list() {
        WebClient aClient = WebClient.builder().baseUrl(listProperties.getUrl()).build();

        String aListJson = aClient.get().retrieve().bodyToMono(String.class).block();
        ObjectMapper objectMapper = new ObjectMapper();

        List<JdkClassification> allJdksAvailable = Collections.emptyList();
        try {
            allJdksAvailable = objectMapper.readValue(aListJson, new TypeReference<>() {
                @Override
                public Type getType() {
                    return new ParameterizedTypeReference<List<JdkClassification>>() {
                    }.getType();
                }
            });
        } catch (JsonProcessingException theCause) {
            LOGGER.error("Error while trying to de-serialised the remote list", theCause);
        }
        
        return allJdksAvailable;
    }
    
    @Override
    public void print(List<JdkClassification> theOutputToRender) {
        System.out.println(theOutputToRender);
    }
    
}
