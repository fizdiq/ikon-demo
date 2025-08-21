package com.fizdiq.ikondemo.controller;

import com.fizdiq.ikondemo.services.IkonDemoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ikon-demo")
@Slf4j
@RequiredArgsConstructor
@Validated
public class IkonDemoController {

    private final IkonDemoService ikonDemoService;


    @GetMapping("")
    public ResponseEntity<?> getIkonData(@RequestParam(value = "page", required = true) @Min(value = 0,
                                                     message = "Page must be 0 or positive") int page,
                                         @RequestParam(value = "size", required = true) @Positive(message = "Size " +
                                                 "must be greater than zero") int size) {

        return ikonDemoService.getIkonData(page, size);
    }
}
