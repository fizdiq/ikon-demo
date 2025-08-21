package com.fizdiq.ikondemo.dto.host;

public record HostResponse(
        int userId,
        int id,
        String title,
        String body) {
}
