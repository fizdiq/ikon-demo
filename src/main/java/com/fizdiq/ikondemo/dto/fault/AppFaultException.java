package com.fizdiq.ikondemo.dto.fault;

import lombok.Getter;

@Getter
public class AppFaultException extends Exception {
    Object faultInfo = null;
    String origin = "";
    String errorCode = "";
    String errorMessage = "";

    public AppFaultException() {
	}

    public AppFaultException(String message, String origin, String errorCode, String errorMessage, Object faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
		this.origin = origin;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
