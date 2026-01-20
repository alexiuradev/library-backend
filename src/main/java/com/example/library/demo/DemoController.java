
package com.example.library.demo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/api/v1/secure/ping")
    public String ping() {
        return "pong";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/admin/ping")
    public String adminPing() {
        return "admin-pong";
    }
}
