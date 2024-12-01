package be.anticair.anticairapi.keycloak.controller;

import be.anticair.anticairapi.keycloak.service.UserService;
import org.apache.catalina.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST Controller for managing users in Keycloak.
 * @Author Blommaert Youry
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    /**
     * Service for performing users-related operations.
     * @Author Blommaert Youry
     */
    private final UserService userService;

    /**
     * Constructor with dependency injection for the UserService.
     *
     * @param userService the service used to manage users in Keycloak.
     * @Author Blommaert Youry
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users from the database.
     *
     * @return a ResponseEntity containing a list of all users.
     * @Author Blommaert Youry
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserRepresentation>> listUsers() {
        List<UserRepresentation> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users from a specific group.
     * @return ResponseEntity containing a list of all users in the group specified.
     * @Author Blommaert Youry
     */
    @GetMapping("/list/admin")
    public ResponseEntity<List<UserRepresentation>> listAdmins() {
        List<UserRepresentation> admins = userService.getUsersByGroupName("Admin");
        return ResponseEntity.ok(admins);
    }

    /**
     * Get all users from a specific group
     * @return ResponseEntity containing a list of all users in the antiquarian group specified.
     * @Author Zarzycki Alexis
     */
    @GetMapping("/list/antiquarian")
    public ResponseEntity<List<UserRepresentation>> listAntiquarian() {
        List<UserRepresentation> antiquarian = userService.getUsersByGroupName("Antiquarian");
        return ResponseEntity.ok(antiquarian);
    }

    /**
     * Desactivate a user
     * @return ResponseEntity containing a Json
     * @Author Zarzycki Alexis
     */
    @PostMapping("/desactivate")
    public ResponseEntity<Map<String,String>> desactivateUser(
            @RequestParam String emailId
    ){
        userService.disableUser(emailId);
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", "User disabled successfully");
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Activate a user
     * @return ResponseEntity containing a Json
     * @Author Zarzycki Alexis
     */
    @PostMapping("/activate")
    public ResponseEntity<Map<String,String>> activateUser(
            @RequestParam String emailId
    ){
        userService.enableUser(emailId);
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", "User enabled successfully");
        return ResponseEntity.ok(responseMessage);
    }


}
