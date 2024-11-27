package be.anticair.anticairapi.keycloak.service;

import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
