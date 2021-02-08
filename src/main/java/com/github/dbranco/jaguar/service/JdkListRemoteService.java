package com.github.dbranco.jaguar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class JdkListRemoteService implements JdkListService {

    public void list() {
        WebClient client = WebClient.builder()
            .baseUrl("https://raw.githubusercontent.com/dbranco/jaguar/main/src/main/resources/list.json")
            .build();

        String bodyToMono = client.get()
            .retrieve()
            .bodyToMono(String.class)
            .block();

        System.out.println("Hello LS" + bodyToMono);
    }
    
}
