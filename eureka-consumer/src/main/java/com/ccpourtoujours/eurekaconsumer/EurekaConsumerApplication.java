package com.ccpourtoujours.eurekaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@EnableHystrix
//  开启扫描spring cloud feign 客户端功能
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class EurekaConsumerApplication {

    // 用来发起rest请求
/*    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }*/

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

}
