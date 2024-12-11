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
import java.util.Objects;


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
     * Get all users without groups from the database.
     *
     * @return a ResponseEntity containing a list of all users without groups.
     * @Author Blommaert Youry
     */
    @GetMapping("/list/users")
    public ResponseEntity<List<UserRepresentation>> listUsersWithoutGroups() {
        List<UserRepresentation> users = userService.getUsersWithoutGroups();
        return ResponseEntity.ok(users);
    }

    /**
     * Get the number of users from the database
     *
     * @return a ResponseEntity containing the number of users
     * @Author Verly Noah
     */
    @GetMapping("/nbrUsers")
    public ResponseEntity<Integer> numberUsers() {
        int nbrUser = userService.getNumberOfUsers();
        return ResponseEntity.ok(nbrUser);
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

    /**
     * Get the status of a user
     * @return ResponseEntity containing a Json
     * @Author Zarzycki Alexis
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getUserStatus(
            @RequestParam String emailId
    ){
        String value = String.valueOf(userService.getUserStatus(emailId));
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", value);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Redistrute the antiquity of a antiquarian
     * @return ResponseEntity containing a Json
     * @Author Verly Noah
     */
    @PutMapping("/redistributeAntiquity")
    public ResponseEntity<Map<String, String>> redistributeAntiquity(
            @RequestParam String emailId
    ){
        String value = String.valueOf(userService.redistributeAntiquity(emailId));
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", value);
        if(Objects.equals(responseMessage.get("message"), "Antiquity's antiquarian changed")){
            return ResponseEntity.ok(responseMessage);
        }
        return ResponseEntity.badRequest().body(responseMessage);
    }

}
