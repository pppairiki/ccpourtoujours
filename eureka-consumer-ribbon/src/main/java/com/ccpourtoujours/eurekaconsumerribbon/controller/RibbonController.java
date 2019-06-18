package com.ccpourtoujours.eurekaconsumerribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/ribbon/consumer")
    public String ribbonConsumer(){
        return restTemplate.getForObject("http://eureka-client/dc", String.class);
    }
}
