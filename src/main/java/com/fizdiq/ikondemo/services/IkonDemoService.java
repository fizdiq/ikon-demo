package com.fizdiq.ikondemo.services;

import org.springframework.http.ResponseEntity;

public interface IkonDemoService {

    ResponseEntity<?> getIkonData(int page, int size);
}
