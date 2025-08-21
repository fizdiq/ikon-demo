package com.fizdiq.ikondemo.services;

import com.fizdiq.ikondemo.dto.host.HostResponse;

import java.io.IOException;
import java.util.List;

public interface HostService {

    List<HostResponse> invokeHost() throws IOException;
}
