package be.anticair.anticairapi.keycloak.controller;

import be.anticair.anticairapi.keycloak.service.GroupService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing user groups in Keycloak.
 * @Author Zarzycki Alexis
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    /**
     * Service for performing group-related operations.
     * @Author Zarzycki Alexis
     */
    private final GroupService groupService;

    /**
     * Constructor with dependency injection for the GroupService.
     *
     * @param groupService the service used to manage groups in Keycloak.
     * @Author Zarzycki Alexis
     */
    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Adds a user to a specified group in Keycloak.
     *
     * @param emailId   the email of the user to be added to the group.
     * @param groupName the name of the group to which the user will be added.
     * @return a ResponseEntity containing a success message.
     * @Author Zarzycki Alexis
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addGroup(
            @RequestParam String emailId,
            @RequestParam String groupName
    ) {
        groupService.addGroup(emailId, groupName);
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", emailId + " added to the group " + groupName);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Removes a user from a specified group in Keycloak.
     *
     * @param emailId   the email of the user to be removed from the group.
     * @param groupName the name of the group from which the user will be removed.
     * @return a ResponseEntity containing a success message.
     * @Author Zarzycki Alexis
     */
    @PostMapping("/remove")
    public ResponseEntity<String> removeGroup(
            @RequestParam String emailId,
            @RequestParam String groupName
    ) {
        groupService.removeGroup(emailId, groupName);
        return ResponseEntity.ok(emailId + " removed from the group " + groupName);
    }

    /**
     * Handles exceptions when a user or group is not found in Keycloak.
     *
     * @param ex the NotFoundException thrown when the user or group is not found.
     * @return a ResponseEntity with a 404 status and an error message.
     * @Author Zarzycki Alexis
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles general runtime exceptions.
     *
     * @param ex the RuntimeException thrown during processing.
     * @return a ResponseEntity with a 500 status and an error message.
     * @Author Zarzycki Alexis
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
