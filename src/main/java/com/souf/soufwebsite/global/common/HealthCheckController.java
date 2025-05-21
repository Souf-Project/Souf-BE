package com.souf.soufwebsite.global.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/v1/normal/check")
    public String healthCheck(){
        return "Health Check";
    }
}
