package com.fizdiq.ikondemo.dto.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHandler {
    private String statusCode;
    private boolean status;
    private String message;
    private Object data;
}
