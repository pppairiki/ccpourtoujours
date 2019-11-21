package com.ccpourtoujours.eurekaconsumer.sao;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HystricEurekaClient {

    @Autowired
    private SEurekaClient sEurekaClient;

    @HystrixCommand(fallbackMethod = "fallback")
    public String consumer() {
        return sEurekaClient.consumer();
    }

    public String fallback() {
        return "fallback";
    }

}
