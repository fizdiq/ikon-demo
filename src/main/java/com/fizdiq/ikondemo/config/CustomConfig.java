package com.fizdiq.ikondemo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomConfig {

    @Value("${ikon.demo.endpoint")
    @Getter
    @Setter
    public static String endpoint;

    private CustomConfig() {}


    @Autowired
    public void loadEndpoint(@Value("${ikon.demo.endpoint}") String endpointLoaded) {
        endpoint = endpointLoaded;
    }
}
