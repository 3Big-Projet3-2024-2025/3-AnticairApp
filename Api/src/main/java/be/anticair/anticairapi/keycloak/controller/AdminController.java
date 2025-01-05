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

    @PostMapping("/force-password-reset/{userEmail}")
    public ResponseEntity<?> forcePasswordReset(@PathVariable String userEmail) {
        adminService.forcePasswordReset(userEmail);
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", "Password has been forced to be reset successfully");
        return ResponseEntity.ok(responseMessage);
    }


}