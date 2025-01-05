package be.anticair.anticairapi.keycloak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import be.anticair.anticairapi.keycloak.service.AdminService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/force-password-reset/{userId}")
    public ResponseEntity<?> forcePasswordReset(@PathVariable String userId) {
        adminService.forcePasswordReset(userId);
        return ResponseEntity.ok("Password reset has been forced successfully.");
    }


}
