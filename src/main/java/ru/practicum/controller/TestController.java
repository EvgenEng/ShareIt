package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/test")
    public String test() {
        log.info("Received request to /test");
        return "OK from ShareIt API!";
    }
}
