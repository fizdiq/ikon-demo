package com.fizdiq.ikondemo.services.impl;

import com.fizdiq.ikondemo.dto.channel.ChannelResponse;
import com.fizdiq.ikondemo.dto.fault.AppFaultException;
import com.fizdiq.ikondemo.dto.handler.ResponseHandler;
import com.fizdiq.ikondemo.dto.host.HostResponse;
import com.fizdiq.ikondemo.services.IkonDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IkonDemoServiceImpl implements IkonDemoService {
    private final HostServiceImpl hostService;

    @Override
    public ResponseEntity<?> getIkonData(int page, int size) {
        try {
            String faultInfo = "Invalid page number or size.";
            List<HostResponse> hostResponseList = hostService.invokeHost();

            int start = page * size;
            int hostResponseCount = hostResponseList.size();
            int end = Math.min(start + size, hostResponseCount);

            if (start >= hostResponseCount) {
                String dataExceededMessage = "Requested page exceeds available data.";
                throw new AppFaultException(dataExceededMessage,
                        "SERVICE",
                        "904",
                        dataExceededMessage,
                        faultInfo);
            }

            List<ChannelResponse> paginatedList =
                    hostResponseList.subList(start, end).stream().map(this::mapToChannelResponse).toList();

            return ResponseEntity.ok(paginatedList);
        } catch (AppFaultException appFaultException) {
            log.error("Error occurred while processing request: {}", appFaultException.getMessage());
            ResponseHandler responseHandler = ResponseHandler.builder()
                    .statusCode(appFaultException.getErrorCode())
                    .status(false)
                    .message(appFaultException.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseHandler);
        } catch (Exception e) {
            log.error("GENERAL ERROR: {}", e.getMessage());
            log.error("ERROR: {} at {}", e, e.getStackTrace()[0].toString());
            ResponseHandler responseHandler = ResponseHandler.builder()
                    .statusCode("999")
                    .status(false)
                    .message("An error occurred while processing request: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseHandler);
        }
    }

    private ChannelResponse mapToChannelResponse(HostResponse hostResponse) {
        return new ChannelResponse(hostResponse.id(), hostResponse.title());
    }
}
