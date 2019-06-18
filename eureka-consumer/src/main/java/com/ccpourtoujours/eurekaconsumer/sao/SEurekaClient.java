package com.ccpourtoujours.eurekaconsumer.sao;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("eureka-client")
public interface SEurekaClient {

    @GetMapping("/dc")
    String consumer();

}
