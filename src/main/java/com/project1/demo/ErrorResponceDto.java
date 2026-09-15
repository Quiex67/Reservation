package com.project1.demo;

import java.time.LocalDateTime;

public record ErrorResponceDto(
        String message,
        String detMessage,
        LocalDateTime errorTime
) {

}
