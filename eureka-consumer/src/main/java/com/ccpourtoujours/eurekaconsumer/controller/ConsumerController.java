package com.ccpourtoujours.eurekaconsumer.controller;

import com.ccpourtoujours.eurekaconsumer.sao.HystricEurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private HystricEurekaClient hystricEurekaClient;

    @GetMapping("/consumer")
    public String consumer() {
        return hystricEurekaClient.consumer();
    }
}
