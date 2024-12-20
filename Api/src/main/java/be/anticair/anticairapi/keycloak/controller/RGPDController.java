package be.anticair.anticairapi.keycloak.controller;


import be.anticair.anticairapi.enumeration.TypeOfMail;
import be.anticair.anticairapi.keycloak.service.EmailService;
import be.anticair.anticairapi.keycloak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rgpd")
public class RGPDController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String sender;

    @PutMapping("/update")
    public ResponseEntity<Map<String,String>> updateRGPD(@RequestBody Map<String, Object> userDetails) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = (String) userDetails.get("email");
            if (email == null || email.isEmpty()) {

                response.put("error", "Email is required.");
                return ResponseEntity.badRequest().body(response);
            }
//            userService.updateRGPDUserProfile(userDetails);
            Map<String,String> otherInformation = new HashMap<>();
           emailService.sendHtmlEmail(email,"info@anticairapp.sixela.be", TypeOfMail.DELETEUSERDATA, otherInformation);
            response.put("message", "User profile updated successfully.");
            return ResponseEntity.ok(response); // Return valid json
        } catch (Exception e) {
            response.put("error", "Error updating user profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
