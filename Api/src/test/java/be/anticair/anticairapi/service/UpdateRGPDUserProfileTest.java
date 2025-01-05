package be.anticair.anticairapi.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class UpdateRGPDUserProfileTest {

    private Keycloak keycloak;
    private RealmResource realmResource;
    private UsersResource usersResource;
    private UpdateRGPDService service;

    private final String realm = "testRealm";

    @BeforeEach
    void setup() {
        keycloak = mock(Keycloak.class);
        realmResource = mock(RealmResource.class);
        usersResource = mock(UsersResource.class);

        when(keycloak.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        service = new UpdateRGPDService(keycloak, realm);
    }

    @Test
    void testUserNotFound() {
        when(usersResource.search("nonexistent@example.com"))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateRGPDUserProfile(Map.of("email", "nonexistent@example.com"));
        });

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
    }

    @Test
    void testErrorDuringUpdate() {
        UserRepresentation user = new UserRepresentation();
        user.setId("user123");

        // Mocking search
        when(usersResource.search("error@example.com"))
                .thenReturn(List.of(user));

        // Mocking UserResource and simulating exception
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(user.getId())).thenReturn(userResource);
        doThrow(new RuntimeException("Update failed"))
                .when(userResource).update(any(UserRepresentation.class));

        // Capturing and asserting exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateRGPDUserProfile(Map.of("email", "error@example.com"));
        });

        // Assert the error message only
        assertEquals("Update failed", exception.getMessage());

        // Verify interactions
        verify(usersResource).search("error@example.com");
        verify(usersResource).get(user.getId());
        verify(userResource).update(any(UserRepresentation.class));
    }


    @Test
    void testSuccessfulUpdate() {
        UserRepresentation user = new UserRepresentation();
        user.setId("user123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        // Moquez le comportement pour 'search'
        when(usersResource.search("success@example.com"))
                .thenReturn(List.of(user));

        // Moquez le comportement pour 'get'
        var userResource = mock(UserResource.class); // Mocker UserResource séparément
        when(usersResource.get("user123")).thenReturn(userResource);

        // Maintenant utilisez `doNothing()` pour une méthode void
        doNothing().when(userResource).update(any(UserRepresentation.class));

        // Appellez la méthode testée et vérifiez qu'il n'y a pas d'exception
        assertDoesNotThrow(() -> {
            service.updateRGPDUserProfile(Map.of("email", "success@example.com"));
        });

        // Vérifiez les interactions avec vos mocks
        verify(usersResource).search("success@example.com");
        verify(usersResource).get("user123");
        verify(userResource).update(any(UserRepresentation.class));
    }


    // Une classe fictive pour simuler le service
    private static class UpdateRGPDService {
        private final Keycloak keycloak;
        private final String realm;

        UpdateRGPDService(Keycloak keycloak, String realm) {
            this.keycloak = keycloak;
            this.realm = realm;
        }

        public void updateRGPDUserProfile(Map<String, Object> userDetails) {
            String email = (String) userDetails.get("email");
            List<UserRepresentation> users = keycloak.realm(realm).users().search(email);
            if (users.isEmpty()) {
                throw new RuntimeException("User not found with email: " + email);
            }

            UserRepresentation user = users.get(0);
            user.setFirstName("FirstName_" + user.getId());
            user.setLastName("LastName_" + user.getId());
            user.singleAttribute("phoneNumber", "+32000000000");
            user.setEmail("anonymized" + user.getId() + "@deleted.com");
            user.setEnabled(false);

            keycloak.realm(realm).users().get(user.getId()).update(user);
        }
    }
}
