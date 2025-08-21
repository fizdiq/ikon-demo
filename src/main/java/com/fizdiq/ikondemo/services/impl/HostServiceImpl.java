package com.fizdiq.ikondemo.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizdiq.ikondemo.config.CustomConfig;
import com.fizdiq.ikondemo.dto.host.HostResponse;
import com.fizdiq.ikondemo.services.HostService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HostServiceImpl implements HostService {
    @Override
    public List<HostResponse> invokeHost() throws IOException {
        long connectTimeout = 20L;
        long readTimeout = 60L;
        String url = CustomConfig.endpoint;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).get().build();

        Call call = client.newCall(request);


        try (Response response = call.execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, HostResponse.class));
        }
    }
}
