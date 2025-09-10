package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    @GetMapping("/")
    public responseDTO hello(Authentication authentication, HttpServletRequest request) {
        String protocol = request.getScheme(); // "http" 또는 "https"

        responseDTO dto = new responseDTO();
        dto.setMessage("Hello, " + authentication.getName());
        dto.setProtocol(protocol);

        return dto;

    }

}