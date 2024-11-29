package be.anticair.anticairapi.keycloak.service;

import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that manage user's
 * @Author Zarzycki Alexis
 **/
@Service
public class UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    public UserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    /**
     * Retrieves a user from his email
     * @param userEmail The Email of the user to find
     * @return List of the users finded
     * @Author Zarzycki Alexis
     */
    public List<UserRepresentation> getUsersByEmail(String userEmail) {
        try {

            // Search for the user in the realm by email
            List<UserRepresentation> users = keycloak.realm(realm).users().search(userEmail);

            // Check if the list is empty
            if (users.isEmpty()) {
                throw new NotFoundException("No users found with email: " + userEmail);
            }

            return users;
        } catch (Exception e) {
            // Debug log for exception
            throw new NotFoundException("Error while retrieving users with email: " + userEmail, e);
        }
    }

    /**
     * Retrieves all users from the realm
     * @return List of all users
     * @Author Blommaert Youry
     */
    public List<UserRepresentation> getAllUsers() {
        try{
            List<UserRepresentation> users = keycloak.realm(realm).users().list();

            if(users.isEmpty()){
                throw new NotFoundException("No users found");
            }

            return users;

        } catch(Exception e){
            throw new NotFoundException("Error while retrieving all users", e);
        }
    }

    /**
     * Retrieves all users from the realm that are in the specified group.
     *
     * @param groupName
     * @return List of all users in the group specified
     * @Author Blommaert Youry
     */
    public List<UserRepresentation> getUsersByGroupName(String groupName) {
        List<UserRepresentation> users = new ArrayList<>();
        try {
            if (groupName == null || groupName.isEmpty()) {
                throw new IllegalArgumentException("Group name must not be null or empty");
            }

            GroupRepresentation group = keycloak.realm(realm).groups().groups().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Group not found: " + groupName));

            users = keycloak.realm(realm).groups().group(group.getId()).members();

            if (users.isEmpty()) {
                throw new NotFoundException("No users found in the group: " + groupName);
            }
        } catch (NotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Error while retrieving users in the group: " + groupName, e);
        }
        return users;
    }



}
