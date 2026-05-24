package com.yashrajput.shieldgate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "ShieldGate is alive";
    }

    @GetMapping("/api/vendor/test")
    public String vendorTest() {
        return "Vendor API Access Granted";
    }
}